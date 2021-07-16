package com.ferelin.stockprice.dataInteractor.workers.searchRequests

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

import com.ferelin.repository.Repository
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 *  [SearchRequestsWorker] ab ability to:
 *   - Observing [mStateSearchRequests] to display a history of searches.
 *   - Observing [mStatePopularSearchRequests] to display populars search requests.
 *
 *  Also [SearchRequestsWorker] do manually:
 *   - Using [mRepository] to data caching.
 *   - Using [mSearchRequestsLimit] to control limit of search requests.
 *   - Optimizing search requests size. @see [removeSearchRequestsDuplicates]
 */

@Singleton
class SearchRequestsWorker @Inject constructor(
    private val mRepository: Repository,
    private val mAppScope: CoroutineScope,
    private val mSearchRequestsSynchronization: SearchRequestsSynchronization
) : SearchRequestsWorkerStates {

    private var mSearchRequests: ArrayList<AdaptiveSearchRequest> = arrayListOf()

    private val mStateSearchRequests =
        MutableStateFlow<DataNotificator<List<AdaptiveSearchRequest>>>(DataNotificator.None())
    override val stateSearchRequests: StateFlow<DataNotificator<List<AdaptiveSearchRequest>>>
        get() = mStateSearchRequests.asStateFlow()

    private val mStatePopularSearchRequests =
        MutableStateFlow<DataNotificator<List<AdaptiveSearchRequest>>>(DataNotificator.None())
    override val statePopularSearchRequests: StateFlow<DataNotificator<List<AdaptiveSearchRequest>>>
        get() = mStatePopularSearchRequests.asStateFlow()

    private val mSearchRequestsLimit = 30

    init {
        prepareSearchRequests()

        mSearchRequestsSynchronization.addOnNewRemoteItemReceived { searchRequestText ->
            cacheNewSearchRequest(searchRequestText)
        }
    }

    fun cacheNewSearchRequest(searchText: String) {
        mAppScope.launch {
            val newSearchRequest = AdaptiveSearchRequest(searchText)
            removeSearchRequestsDuplicates(newSearchRequest)

            mSearchRequests.add(0, newSearchRequest)
            mSearchRequestsSynchronization.onSearchRequestAdded(newSearchRequest)

            if (isSearchRequestsLimitExceeded()) {
                reduceRequestsToLimit()
            }

            mStateSearchRequests.value = DataNotificator.DataUpdated(mSearchRequests)
            mRepository.cacheSearchRequestsHistoryToLocalDb(mSearchRequests)
        }
    }


    fun onNetworkAvailable() {
        if (mStateSearchRequests.value is DataNotificator.DataPrepared) {
            mSearchRequestsSynchronization.onNetworkAvailable(mSearchRequests)
        }
    }

    fun onNetworkLost() {
        mSearchRequestsSynchronization.onNetworkLost()
    }

    fun onLogIn() {
        mAppScope.launch { prepareSearchRequests() }
    }

    fun onLogOut() {
        clearSearchRequests()
    }

    private fun prepareSearchRequests() {
        mAppScope.launch {
            mStateSearchRequests.value = DataNotificator.Loading()
            mStatePopularSearchRequests.value = DataNotificator.Loading()

            val searchRequestsResponse = mRepository.getSearchesHistoryFromLocalDb()
            if (searchRequestsResponse is RepositoryResponse.Success) {
                mSearchRequests = ArrayList(searchRequestsResponse.data)
                mSearchRequestsSynchronization.onDataPrepared(searchRequestsResponse.data)
                mStateSearchRequests.value = DataNotificator.DataPrepared(mSearchRequests)
                mStatePopularSearchRequests.value =
                    DataNotificator.DataPrepared(PopularRequestsSource.popularSearchRequests)
            } else {
                mStateSearchRequests.value = DataNotificator.None()
                mStatePopularSearchRequests.value = DataNotificator.None()
            }
        }
    }

    private fun clearSearchRequests() {
        mAppScope.launch {
            mSearchRequests.clear()
            mStateSearchRequests.value = DataNotificator.Loading()
            mRepository.clearLocalSearchRequestsHistory()
        }
    }

    /*
    * Example:
    *   Source: [App, Appl, Ap, Facebook, Apple]
    *   Input:  Apple
    *   Output: [Apple, Facebook]
    * */
    private fun removeSearchRequestsDuplicates(newSearchRequest: AdaptiveSearchRequest) {
        var endBorder = mSearchRequests.size
        var listCursor = 0
        val newSearchRequestStr = newSearchRequest.searchText.toLowerCase(Locale.ROOT)

        while (listCursor < endBorder) {
            val searchRequestStr = mSearchRequests[listCursor].searchText.toLowerCase(Locale.ROOT)
            if (newSearchRequestStr.contains(searchRequestStr)) {
                mSearchRequests.removeAt(listCursor)
                mSearchRequestsSynchronization.onSearchRequestRemoved(mSearchRequests[listCursor])
                endBorder--
            }
            listCursor++
        }
    }

    private fun reduceRequestsToLimit() {
        mSearchRequestsSynchronization.onSearchRequestRemoved(mSearchRequests.last())
        mSearchRequests.removeLast()
    }

    private fun isSearchRequestsLimitExceeded(): Boolean {
        return mSearchRequests.size > mSearchRequestsLimit
    }
}