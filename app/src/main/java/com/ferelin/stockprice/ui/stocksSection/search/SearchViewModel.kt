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
import com.ferelin.stockprice.ui.stocksSection.base.BaseStocksViewModel
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.withTimer
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class SearchViewModel : BaseStocksViewModel() {

    private var mCompanies: ArrayList<AdaptiveCompany>? = null

    val searchRequestAdapter = SearchRequestsAdapter().apply {
        setHasStableIds(true)
    }
    val popularRequestsAdapter = SearchRequestsAdapter().apply {
        setHasStableIds(true)
    }

    private val mStateSearchStockResults = MutableStateFlow<ArrayList<AdaptiveCompany>?>(null)
    val stateSearchStockResults: StateFlow<ArrayList<AdaptiveCompany>?>
        get() = mStateSearchStockResults.asStateFlow()

    private var mLastSearchRequest = ""
    val lastSearchRequest: String
        get() = mLastSearchRequest

    val eventOnError: SharedFlow<String>
        get() = mDataInteractor.sharedLoadSearchRequestsError

    var savedViewTransitionState = 0

    override fun initObserversBlock() {
        super.initObserversBlock()
        viewModelScope.launch(mCoroutineContext.IO) {
            launch { collectStateCompanies() }
            launch { collectStateSearchRequests() }
            launch { collectStatePopularSearchRequests() }
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

    private suspend fun collectStateSearchRequests() {
        mDataInteractor.stateSearchRequests.collect { notificator ->
            if (notificator is DataNotificator.DataPrepared) {
                withContext(mCoroutineContext.Main) {
                    searchRequestAdapter.setData(notificator.data!!)
                }
            }
        }
    }

    private suspend fun collectStatePopularSearchRequests() {
        mDataInteractor.statePopularSearchRequests.collect { notificator ->
            if (notificator is DataNotificator.DataPrepared) {
                withContext(mCoroutineContext.Main) {
                    popularRequestsAdapter.setData(notificator.data!!)
                }
            }
        }
    }

    private suspend fun onNewSearch(searchText: String, resultsSize: Int) {
        if (resultsSize in 1..5) {
            mDataInteractor.cacheSearchRequest(searchText)
        }
    }

    private fun search(searchText: String): MutableList<AdaptiveCompany> {
        val itemsToSearchIn = if (searchText.length > mLastSearchRequest.length) {
            stocksRecyclerAdapter.companies
        } else mCompanies

        val results = mutableListOf<AdaptiveCompany>()
        itemsToSearchIn?.forEach { company ->
            if (filterCompanies(company, searchText)) {
                results.add(company)
            }
        }
        return results
    }

    private fun filterCompanies(item: AdaptiveCompany, text: String): Boolean {
        return item.companyProfile.name
            .lowercase(Locale.ROOT)
            .contains(text.lowercase(Locale.ROOT))
                || item.companyProfile.symbol
            .lowercase(Locale.ROOT)
            .contains(text.lowercase(Locale.ROOT))
    }
}