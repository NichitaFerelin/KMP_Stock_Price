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

package com.ferelin.stockprice.dataInteractor.syncManager.helpers

import com.ferelin.repository.Repository
import com.ferelin.stockprice.dataInteractor.dataManager.workers.searchRequests.SearchRequestsWorker
import com.ferelin.stockprice.dataInteractor.syncManager.SyncConflictMode
import com.ferelin.stockprice.utils.actionHolder.ActionHolder
import com.ferelin.stockprice.utils.actionHolder.ActionType
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [SearchRequestsSyncHelper] holds logic about search requests synchronization
 * with realtime database.
 * @param mDataMediator is used to notify about new search requests.
 * @param mRepository is used to work with realtime database
 * */
@Singleton
class SearchRequestsSyncHelper @Inject constructor(
    private val mRepository: Repository,
    private val mSearchRequestsWorker: SearchRequestsWorker
) {
    /*
    * Container for search requests which is at realtime database.
    * Directly is used to detect inconsistency between local and remote items.
    * */
    private var mRemoteSearchRequestsContainer = mutableListOf<String>()

    // User authentication ID
    private lateinit var mUserId: String

    // Synchronization mode to define how to sync data
    private lateinit var mSyncMode: SyncConflictMode

    /**
     * Prepare for search requests synchronization with realtime database.
     * @param userId is a user authentication ID
     * @param syncMode is a synchronization mode of data. Is set to mode "merge" by default.
     */
    fun prepareToSync(userId: String, syncMode: SyncConflictMode = SyncConflictMode.Merge) {
        mUserId = userId
        mSyncMode = syncMode
        mRemoteSearchRequestsContainer.clear()
    }

    /**
     * Provides ability to notify about a new loaded search request from realtime database.
     *
     *
     * This synchronization step searches for a remote search request among local requests ->
     * depending on the [mSyncMode], the search request can be removed from real-time database or
     * added to local database.
     * @param searchRequest is a search request that was loaded and read from response.
     * */
    suspend fun onSearchRequestResponseSync(searchRequest: String) {
        val localSearchRequests = mSearchRequestsWorker.searchRequests.toList()
        val remoteSearchRequestAtLocal = localSearchRequests.find { it.searchText == searchRequest }

        if (remoteSearchRequestAtLocal == null) {
            when (mSyncMode) {
                is SyncConflictMode.LocalPriority -> {
                    mRepository.eraseSearchRequestFromRealtimeDb(mUserId, searchRequest)
                }
                else -> {
                    mRemoteSearchRequestsContainer.add(searchRequest)
                    mSearchRequestsWorker.cacheNewSearchRequest(searchRequest)
                }
            }
        }
    }

    /**
     * Final step on synchronization.
     * This step is based on [mSyncMode] and [mRemoteSearchRequestsContainer]
     * and is not required if sync mode is with Remote Priority.
     *
     * @see detectInconsistencyAndSync
     * */
    fun onSyncEnd() {
        if (mSyncMode !is SyncConflictMode.RemotePriority) {
            detectInconsistencyAndSync()
        }
    }

    /**
     * Provides ability to notify real-time database that search request was removed/added to
     * local database. Used directly when user side a new search.
     *
     * @param changesActionsHistory is a actions-container with steps that was invoked while
     * new search request has been cached. This steps must be repeated on real-time database.
     * Example: [ ("abc", Removed), ("bbb", Added) ]
     * */
    fun onSearchRequestsChanged(changesActionsHistory: List<ActionHolder<String>>) {
        mRepository.getUserAuthenticationId()?.let { authorizedUserId ->
            changesActionsHistory.forEach { actionHolder ->
                when (actionHolder.actionType) {
                    is ActionType.Added -> {
                        if (!mRemoteSearchRequestsContainer.contains(actionHolder.key)) {
                            mRemoteSearchRequestsContainer.add(actionHolder.key)
                            mRepository.cacheSearchRequestToRealtimeDb(
                                authorizedUserId,
                                actionHolder.key
                            )
                        }
                    }
                    is ActionType.Removed -> {
                        if (mRemoteSearchRequestsContainer.contains(actionHolder.key)) {
                            mRemoteSearchRequestsContainer.remove(actionHolder.key)
                            mRepository.eraseSearchRequestFromRealtimeDb(
                                authorizedUserId,
                                actionHolder.key
                            )
                        }
                    }
                }
            }
        }
    }

    /**
     * When the user exits need to call this method to clear his data
     */
    suspend fun onLogOut() {
        mSearchRequestsWorker.clearSearchRequests()
    }

    /**
     * On this synchronization step [mRemoteSearchRequestsContainer] is completely filled with data
     * from real-time database. Now need to find missing local search requests which was not
     * added to real-time database.
     * */
    private fun detectInconsistencyAndSync() {
        val localSearchRequests = mSearchRequestsWorker.stateSearchRequests.value.data ?: return

        if (mRemoteSearchRequestsContainer.isEmpty()) {
            localSearchRequests
                .map { it.searchText }
                .forEach { request ->
                    mRepository.cacheSearchRequestToRealtimeDb(mUserId, request)
                }
        } else {
            localSearchRequests.forEach { searchRequest ->
                val itemAtRemoteContainer =
                    mRemoteSearchRequestsContainer.find { it == searchRequest.searchText }

                if (itemAtRemoteContainer == null) {
                    mRepository.cacheSearchRequestToRealtimeDb(
                        mUserId,
                        searchRequest.searchText
                    )
                }
            }
        }
    }
}