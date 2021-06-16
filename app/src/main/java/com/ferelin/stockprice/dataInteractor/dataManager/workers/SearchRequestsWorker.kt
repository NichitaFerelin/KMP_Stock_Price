package com.ferelin.stockprice.dataInteractor.dataManager.workers

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
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.actionHolder.ActionHolder
import com.ferelin.stockprice.utils.actionHolder.ActionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.ArrayList

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
    private val mRepository: Repository
) {
    private var mSearchRequests: ArrayList<AdaptiveSearchRequest> = arrayListOf()
    val searchRequests: List<AdaptiveSearchRequest>
        get() = mSearchRequests.toList()

    private val mSearchRequestsLimit = 30

    private val mStateSearchRequests =
        MutableStateFlow<DataNotificator<ArrayList<AdaptiveSearchRequest>>>(DataNotificator.Loading())
    val stateSearchRequests: StateFlow<DataNotificator<ArrayList<AdaptiveSearchRequest>>>
        get() = mStateSearchRequests

    /*
    * Mocked data
    * */
    private var mPopularSearchRequests: ArrayList<AdaptiveSearchRequest> = arrayListOf(
        AdaptiveSearchRequest("Apple"),
        AdaptiveSearchRequest("Microsoft Corp"),
        AdaptiveSearchRequest("Amazon.com"),
        AdaptiveSearchRequest("Alphabet"),
        AdaptiveSearchRequest("JD.com"),
        AdaptiveSearchRequest("Tesla"),
        AdaptiveSearchRequest("Facebook"),
        AdaptiveSearchRequest("Telefonaktiebolaget"),
        AdaptiveSearchRequest("NVIDIA"),
        AdaptiveSearchRequest("Beigene"),
        AdaptiveSearchRequest("Intel"),
        AdaptiveSearchRequest("Netflix"),
        AdaptiveSearchRequest("Adobe"),
        AdaptiveSearchRequest("Cisco"),
        AdaptiveSearchRequest("Yandex"),
        AdaptiveSearchRequest("Zoom"),
        AdaptiveSearchRequest("Starbucks"),
        AdaptiveSearchRequest("Charter"),
        AdaptiveSearchRequest("Sanofi"),
        AdaptiveSearchRequest("Amgen"),
        AdaptiveSearchRequest("Pepsi")
    )

    private val mStatePopularSearchRequests =
        MutableStateFlow(DataNotificator.DataPrepared(mPopularSearchRequests))
    val statePopularSearchRequests: StateFlow<DataNotificator<ArrayList<AdaptiveSearchRequest>>>
        get() = mStatePopularSearchRequests

    fun onDataPrepared(searches: List<AdaptiveSearchRequest>) {
        mSearchRequests = ArrayList(searches)
        mStateSearchRequests.value = DataNotificator.DataPrepared(mSearchRequests)
    }

    suspend fun cacheNewSearchRequest(searchText: String): List<ActionHolder<String>> {
        val newSearchRequest = AdaptiveSearchRequest(searchText)
        val actionsContainer = mutableListOf<ActionHolder<String>>()

        removeSearchRequestsDuplicates(newSearchRequest, actionsContainer)
        mSearchRequests.add(0, newSearchRequest)
        actionsContainer.add(ActionHolder(ActionType.Added, searchText))

        if (isSearchRequestsLimitExceeded()) {
            reduceRequestsToLimit(actionsContainer)
        }

        mStateSearchRequests.value = DataNotificator.DataUpdated(mSearchRequests)
        mRepository.setSearchesHistory(mSearchRequests)

        return actionsContainer
    }

    suspend fun clearSearchRequests() {
        mSearchRequests.clear()
        mStateSearchRequests.value = DataNotificator.Loading()
        mRepository.clearSearchesHistory()
    }

    /*
    * Example:
    *   Source: [App, Appl, Ap, Facebook, Apple]
    *   Input:  Apple
    *   Output: [Apple, Facebook]
    * */
    private fun removeSearchRequestsDuplicates(
        newSearchRequest: AdaptiveSearchRequest,
        actionsContainer: MutableList<ActionHolder<String>>
    ) {
        var endBorder = mSearchRequests.size
        var listCursor = 0
        val newSearchRequestStr = newSearchRequest.searchText.toLowerCase(Locale.ROOT)
        while (listCursor < endBorder) {
            val searchRequestStr = mSearchRequests[listCursor].searchText.toLowerCase(Locale.ROOT)
            if (newSearchRequestStr.contains(searchRequestStr)) {
                val action =
                    ActionHolder(ActionType.Removed, mSearchRequests[listCursor].searchText)
                actionsContainer.add(action)
                mSearchRequests.removeAt(listCursor)
                endBorder--
            }
            listCursor++
        }
    }

    private fun reduceRequestsToLimit(actionsContainer: MutableList<ActionHolder<String>>) {
        val lastItemText = mSearchRequests.last().searchText
        val action = ActionHolder(ActionType.Removed, lastItemText)
        actionsContainer.add(action)
        mSearchRequests.removeLast()
    }

    private fun isSearchRequestsLimitExceeded(): Boolean {
        return mSearchRequests.size > mSearchRequestsLimit
    }
}