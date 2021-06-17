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
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RelationsWorkerImpl @Inject constructor(
    private val mRepository: Repository
) : RelationsWorker, RelationsWorkerStates {

    private var mRelations = mutableListOf<AdaptiveRelation>()

    private val mStateUserRelations =
        MutableStateFlow<DataNotificator<List<AdaptiveRelation>>>(DataNotificator.Loading())
    override val stateUserRelations: StateFlow<DataNotificator<List<AdaptiveRelation>>>
        get() = mStateUserRelations

    private val mSharedUserRelationsUpdates = MutableSharedFlow<DataNotificator<AdaptiveRelation>>()
    override val sharedUserRelationsUpdates: SharedFlow<DataNotificator<AdaptiveRelation>>
        get() = mSharedUserRelationsUpdates

    override fun onRelationsPrepared(items: List<AdaptiveRelation>) {
        mRelations = items.toMutableList()
        mStateUserRelations.value = DataNotificator.DataPrepared(mRelations)
    }

    override suspend fun createNewRelation(associatedUserLogin: String) {
        val newRelation = AdaptiveRelation(
            id = mRelations.size,
            associatedUserLogin = associatedUserLogin
        )

        mRelations.add(newRelation)
        mSharedUserRelationsUpdates.emit(DataNotificator.NewItemAdded(newRelation))
        mRepository.cacheRelationToLocalDb(newRelation)
    }

    override suspend fun removeRelation(relation: AdaptiveRelation) {
        val target = mRelations.binarySearchBy(relation.id) { it.id }
        mRelations.removeAt(target)
        mSharedUserRelationsUpdates.emit(DataNotificator.ItemRemoved(relation))
        mRepository.eraseRelationFromLocalDb(relation)
    }
}