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

package com.ferelin.stockprice.dataInteractor.workers.searchRequests

import com.ferelin.repository.Repository
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.dataInteractor.utils.SyncConflictMode
import kotlinx.coroutines.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [SearchRequestsSynchronization] holds logic about search requests synchronization
 * with realtime database.
 * @param mDataMediator is used to notify about new search requests.
 * @param mRepository is used to work with realtime database
 * */
@Singleton
class SearchRequestsSynchronization @Inject constructor(
    private val mRepository: Repository,
    private val mAppScope: CoroutineScope
) {
    /*
    * Container for search requests which is at realtime database.
    * Directly is used to detect inconsistency between local and remote items.
    * */
    private var mRemoteSearchRequestsContainer = mutableListOf<String>()

    /*
    * Synchronization mode to define how to sync data.
    * Cannot be changed now.
    */
    private val mSyncMode: SyncConflictMode = SyncConflictMode.Merge

    private var mOnNewRemoteItemReceived: ((String) -> Unit)? = null
    private var mIsDataSynchronized: Boolean = false

    fun initDataSync(localSearchRequests: List<AdaptiveSearchRequest>) {
        mAppScope.launch {
            if (mIsDataSynchronized) {
                return@launch
            }

            mRepository.getUserAuthenticationId()?.let { userToken ->
                val requestsResponse = mRepository.getSearchRequestsFromRealtimeDb(userToken)
                if (requestsResponse is RepositoryResponse.Success) {
                    findAndFixMissingItems(userToken, requestsResponse.data, localSearchRequests)
                    if (mSyncMode !is SyncConflictMode.RemotePriority) {
                        detectInconsistencyAndSync(userToken, localSearchRequests)
                    }
                    mIsDataSynchronized = true
                }
            }
        }
    }

    fun addOnNewRemoteItemReceived(onReceived: (String) -> Unit) {
        mOnNewRemoteItemReceived = onReceived
    }

    fun onNewItem(request: AdaptiveSearchRequest) {
        if (!mRemoteSearchRequestsContainer.contains(request.searchText)) {
            mRemoteSearchRequestsContainer.add(request.searchText)

            mAppScope.launch {
                mRepository.getUserAuthenticationId()?.let { userToken ->
                    mRepository.cacheSearchRequestToRealtimeDb(userToken, request.searchText)
                }
            }
        }
    }

    fun onRemoveItem(request: AdaptiveSearchRequest) {
        if (mRemoteSearchRequestsContainer.contains(request.searchText)) {
            mRemoteSearchRequestsContainer.remove(request.searchText)

            mAppScope.launch {
                mRepository.getUserAuthenticationId()?.let { userToken ->
                    mRepository.eraseSearchRequestFromRealtimeDb(userToken, request.searchText)
                }
            }
        }
    }

    fun onNetworkLost() {
        invalidate()
    }

    fun onLogOut() {
        invalidate()
    }

    private fun findAndFixMissingItems(
        userToken: String,
        remoteItems: List<String>,
        localItems: List<AdaptiveSearchRequest>
    ) {
        remoteItems.forEach { remoteItem ->
            val remoteItemAtLocal = localItems.find { it.searchText == remoteItem }
            if (remoteItemAtLocal == null) {
                when (mSyncMode) {
                    is SyncConflictMode.LocalPriority -> {
                        mRepository.eraseSearchRequestFromRealtimeDb(userToken, remoteItem)
                    }
                    else -> {
                        mRemoteSearchRequestsContainer.add(remoteItem)
                        mOnNewRemoteItemReceived?.invoke(remoteItem)
                    }
                }
            }
        }
    }

    /**
     * On this synchronization step [mRemoteSearchRequestsContainer] is completely filled with data
     * from real-time database. Now need to find missing local search requests which was not
     * added to real-time database.
     * */
    private fun detectInconsistencyAndSync(
        userToken: String,
        localSearchRequests: List<AdaptiveSearchRequest>
    ) {
        when {
            mRemoteSearchRequestsContainer.isEmpty() -> {
                localSearchRequests.forEach { localRequest ->
                    mRepository.cacheSearchRequestToRealtimeDb(userToken, localRequest.searchText)
                }
            }
            else -> {
                localSearchRequests.forEach { localRequest ->
                    val localRequestAtRemoteContainer =
                        mRemoteSearchRequestsContainer.find { it == localRequest.searchText }
                    if (localRequestAtRemoteContainer == null) {
                        mRepository.cacheSearchRequestToRealtimeDb(
                            userToken,
                            localRequest.searchText
                        )
                    }
                }
            }
        }
    }

    private fun invalidate() {
        mIsDataSynchronized = false
        mRemoteSearchRequestsContainer.clear()
    }
}