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

package com.ferelin.stockprice.dataInteractor.workers.companies.favourites

import com.ferelin.repository.Repository
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.dataInteractor.utils.SyncConflictMode
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavouriteCompaniesSynchronization @Inject constructor(
    private val mRepository: Repository,
    private val mAppScope: CoroutineScope
) {
    /*
    * Container for favourite companies which is at realtime database.
    * Directly is used to detect inconsistency between local and remote items.
    * */
    private var mRemoteCompaniesContainer = mutableListOf<AdaptiveCompany>()

    /*
    * Synchronization mode to define how to sync data.
    * Will be available to select mode in future.
    */
    private val mSyncMode: SyncConflictMode = SyncConflictMode.Merge

    private var mIsDataSynchronized: Boolean = false

    fun initDataSync(
        companies: List<AdaptiveCompany>,
        favouriteCompanies: List<AdaptiveCompany>,
        addCompanyToFavourites: suspend (AdaptiveCompany) -> AdaptiveCompany?
    ) {
        mAppScope.launch {
            if (mIsDataSynchronized) {
                return@launch
            }

            mRepository.getUserAuthenticationId()?.let { userToken ->
                val companiesIdsResponse = mRepository.getCompaniesIdsFromRealtimeDb(userToken)
                if (companiesIdsResponse is RepositoryResponse.Success) {
                    findAndFixMissingItems(
                        userToken,
                        companies,
                        companiesIdsResponse.data,
                        addCompanyToFavourites
                    )
                    if (mSyncMode != SyncConflictMode.RemotePriority) {
                        detectInconsistencyAndSync(userToken, favouriteCompanies)
                    }
                    mIsDataSynchronized = true
                }
            }
        }
    }

    /**
     * This method provides ability to notify real-time database that company has been removed
     * from favourites. Can be used directly when user removes company from favourites.
     *
     * Also it can be called while synchronization method is running. To avoid unnecessary
     * access to the real-time database -> use [mRemoteCompaniesContainer] before.
     */
    fun onCompanyRemovedFromLocal(company: AdaptiveCompany) {
        mRepository.getUserAuthenticationId()?.let { authorizedUserId ->
            if (mRemoteCompaniesContainer.contains(company)) {
                mRemoteCompaniesContainer.remove(company)
                mRepository.eraseCompanyIdFromRealtimeDb(
                    authorizedUserId,
                    company.id.toString()
                )
            }
        }
    }

    /**
     * Provides ability to notify real-time database that company has been added to favourites.
     * Can be used directly when user add company to favourites.
     *
     * Also it can be called while synchronization method is running. To avoid unnecessary
     * access to the real-time database -> use [mRemoteCompaniesContainer] before.
     */
    fun onCompanyAddedToLocal(company: AdaptiveCompany) {
        mRepository.getUserAuthenticationId()?.let { authorizedUserId ->
            if (!mRemoteCompaniesContainer.contains(company)) {
                mRemoteCompaniesContainer.add(company)
                mRepository.cacheCompanyIdToRealtimeDb(
                    authorizedUserId,
                    company.id.toString()
                )
            }
        }
    }

    fun onNetworkLost() {
        invalidate()
    }

    fun onLogOut() {
        invalidate()
    }

    private fun invalidate() {
        mIsDataSynchronized = false
        mRemoteCompaniesContainer.clear()
    }

    /**
     * Provides ability to notify about a new loaded favourite company from realtime database.
     *
     *
     * This synchronization step searches for a remote favourite company among local companies ->
     * depending on the 'isFavourite' field and [mSyncMode], the remote company
     * can be removed from real-time database or added to local database.
     * */
    private fun findAndFixMissingItems(
        userToken: String,
        localCompanies: List<AdaptiveCompany>,
        remoteCompaniesIds: List<String>,
        addCompanyToFavourites: suspend (AdaptiveCompany) -> AdaptiveCompany?
    ) {
        remoteCompaniesIds.forEach { remoteCompanyId ->
            val currentRemoteId = remoteCompanyId.toInt()
            val remoteItemAtLocal = localCompanies[currentRemoteId]
            if (!remoteItemAtLocal.isFavourite) {
                when (mSyncMode) {
                    SyncConflictMode.LocalPriority -> {
                        mAppScope.launch {
                            mRepository.eraseCompanyIdFromRealtimeDb(userToken, remoteCompanyId)
                        }
                    }
                    else -> {
                        mAppScope.launch {
                            val addedCompany = addCompanyToFavourites.invoke(remoteItemAtLocal)
                            if (addedCompany != null) {
                                mRemoteCompaniesContainer.add(addedCompany)
                            }
                        }
                    }
                }
            }
        }
    }

    /**
     * On this synchronization step [mRemoteCompaniesContainer] is completely filled with data
     * from real-time database. Now need to find missing local favourite companies which was not
     * added to real-time database.
     * */
    private fun detectInconsistencyAndSync(
        userToken: String,
        favouriteCompanies: List<AdaptiveCompany>
    ) {
        if (mRemoteCompaniesContainer.isEmpty()) {
            favouriteCompanies
                .toList()
                .map { it.id.toString() }
                .forEach { id -> mRepository.cacheCompanyIdToRealtimeDb(userToken, id) }
        } else {
            favouriteCompanies.toList().forEach { localCompany ->
                val indexAtRemoteContainer = mRemoteCompaniesContainer
                    .binarySearchBy(localCompany.id) { it.id }
                if (indexAtRemoteContainer < 0) {
                    mRepository.cacheCompanyIdToRealtimeDb(userToken, localCompany.id.toString())
                }
            }
        }
    }
}