/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ferelin.stockprice.dataInteractor.dataManager.workers.relations

import com.ferelin.repository.Repository
import com.ferelin.repository.adaptiveModels.AdaptiveRelation
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.dataInteractor.dataManager.workers.register.RegisterWorker
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RelationsWorker @Inject constructor(
    private val mRepository: Repository,
    private val mRegisterWorker: RegisterWorker
) : RelationsWorkerStates {

    private var mRelations = mutableListOf<AdaptiveRelation>()

    private val mStateUserRelations =
        MutableStateFlow<DataNotificator<List<AdaptiveRelation>>>(DataNotificator.Loading())
    override val stateUserRelations: StateFlow<DataNotificator<List<AdaptiveRelation>>>
        get() = mStateUserRelations

    private val mSharedUserRelationsUpdates = MutableSharedFlow<DataNotificator<AdaptiveRelation>>()
    override val sharedUserRelationsUpdates: SharedFlow<DataNotificator<AdaptiveRelation>>
        get() = mSharedUserRelationsUpdates

    suspend fun prepareUserRelations() {
        if (!mRepository.isUserAuthenticated()) {
            return
        }

        val relationsResponse = mRepository.getAllRelationsFromLocalDb()
        if (relationsResponse is RepositoryResponse.Success) {

            if (relationsResponse.data.isEmpty()) {
                loadRelationsFromRemote()
            } else onRelationsPrepared(relationsResponse.data)
        }
    }

    fun onLogOut() {
        mRelations.clear()
        mStateUserRelations.value = DataNotificator.NoData()
    }

    suspend fun onLogIn() {
        prepareUserRelations()
    }

    suspend fun createNewRelation(sourceUserLogin: String, associatedUserLogin: String) {
        if (mRelations.find { it.associatedUserLogin == associatedUserLogin } != null) {
            return
        }

        val newRelation = AdaptiveRelation(
            id = mRelations.size,
            associatedUserLogin = associatedUserLogin
        )

        mRelations.add(newRelation)
        mSharedUserRelationsUpdates.emit(DataNotificator.NewItemAdded(newRelation))

        mRepository.cacheRelationToLocalDb(newRelation)
        mRepository.cacheNewRelationToRealtimeDb(
            sourceUserLogin = sourceUserLogin,
            secondSideUserLogin = associatedUserLogin,
            relationId = newRelation.id.toString()
        )
    }

    suspend fun removeRelation(sourceUserLogin: String, relation: AdaptiveRelation) {
        val target = mRelations.binarySearchBy(relation.id) { it.id }
        mRelations.removeAt(target)
        mSharedUserRelationsUpdates.emit(DataNotificator.ItemRemoved(relation))

        mRepository.eraseRelationFromLocalDb(relation)
        mRepository.eraseRelationFromRealtimeDb(sourceUserLogin, relation.id.toString())
    }

    private fun onRelationsPrepared(items: List<AdaptiveRelation>) {
        mRelations = items.toMutableList()
        mStateUserRelations.value = DataNotificator.DataPrepared(mRelations)
    }

    private suspend fun loadRelationsFromRemote() {
        mRegisterWorker.userLogin?.let { userLogin ->
            val remoteRelations = mRepository.getUserRelationsFromRealtimeDb(userLogin)
            if (remoteRelations is RepositoryResponse.Success) {
                onRelationsPrepared(remoteRelations.data)
                remoteRelations.data.forEach {
                    mRepository.cacheNewRelationToRealtimeDb(
                        relationId = it.id.toString(),
                        sourceUserLogin = userLogin,
                        secondSideUserLogin = it.associatedUserLogin
                    )
                }
            }
        }
    }
}