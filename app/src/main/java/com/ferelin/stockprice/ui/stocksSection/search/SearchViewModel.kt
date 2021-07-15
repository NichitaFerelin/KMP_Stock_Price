package com.ferelin.stockprice.ui.stocksSection.search

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

import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksViewModel
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.filterCompanies
import com.ferelin.stockprice.utils.withTimer
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SearchViewModel : BaseStocksViewModel() {

    private var mCompanies: ArrayList<AdaptiveCompany>? = null

    private val mSearchRequestsAdapter = SearchRequestsAdapter()
    val searchRequestAdapter: SearchRequestsAdapter
        get() = mSearchRequestsAdapter

    private val mPopularRequestsAdapter = SearchRequestsAdapter()
    val popularRequestsAdapter: SearchRequestsAdapter
        get() = mPopularRequestsAdapter

    private val mStateSearchStockResults = MutableStateFlow<ArrayList<AdaptiveCompany>?>(null)
    val stateSearchStockResults: StateFlow<ArrayList<AdaptiveCompany>?>
        get() = mStateSearchStockResults

    val stateSearchRequests: StateFlow<DataNotificator<List<AdaptiveSearchRequest>>?>
        get() = mDataInteractor.stateSearchRequests

    val statePopularSearchRequests: StateFlow<DataNotificator<List<AdaptiveSearchRequest>>>
        get() = mDataInteractor.statePopularSearchRequests

    private var mLastSearchRequest = ""
    val lastSearchRequest: String
        get() = mLastSearchRequest

    val eventOnError: SharedFlow<String>
        get() = mDataInteractor.sharedLoadSearchRequestsError

    var savedViewTransitionState = 0

    override fun initObserversBlock() {
        super.initObserversBlock()
        viewModelScope.launch(mCoroutineContext.IO) {
            collectStateCompanies()
        }
    }

    fun onSearchTextChanged(searchText: String) {
        if (searchText == mLastSearchRequest) {
            return
        }

        /*
        * To avoid fast input
        * */
        withTimer(time = 300L) {
            viewModelScope.launch(mCoroutineContext.IO) {
                mLastSearchRequest = searchText

                if (mLastSearchRequest.isEmpty()) {
                    mStateSearchStockResults.value = null
                } else {
                    val searchResults = search(searchText)
                    onNewSearch(searchText, searchResults.size)
                    mStateSearchStockResults.value = ArrayList(searchResults)
                }
            }
        }
    }

    private suspend fun collectStateCompanies() {
        mDataInteractor.stateCompanies
            .filter { it is DataNotificator.DataPrepared && it.data != null }
            .take(1)
            .collect { mCompanies = ArrayList(it.data!!) }
    }

    private suspend fun onNewSearch(searchText: String, resultsSize: Int) {
        if (resultsSize in 1..5) {
            viewModelScope.launch(mCoroutineContext.IO) {
                mDataInteractor.cacheNewSearchRequest(searchText)
            }
        }
    }

    private fun search(searchText: String): MutableList<AdaptiveCompany> {
        val itemsToSearchIn = if (searchText.length > mLastSearchRequest.length) {
            mStocksRecyclerAdapter.companies
        } else mCompanies

        val results = mutableListOf<AdaptiveCompany>()
        itemsToSearchIn?.forEach { company ->
            if (filterCompanies(company, searchText)) {
                results.add(company)
            }
        }
        return results
    }
}