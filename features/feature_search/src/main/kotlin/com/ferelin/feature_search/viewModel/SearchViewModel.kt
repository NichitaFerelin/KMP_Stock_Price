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

package com.ferelin.feature_search.viewModel

import androidx.lifecycle.viewModelScope
import com.ferelin.core.adapter.BaseRecyclerAdapter
import com.ferelin.core.mapper.StockMapper
import com.ferelin.core.utils.LoadState
import com.ferelin.core.utils.ifPrepared
import com.ferelin.core.viewData.StockViewData
import com.ferelin.core.viewModel.BaseStocksViewModel
import com.ferelin.domain.interactors.StockPriceInteractor
import com.ferelin.domain.interactors.companies.CompaniesInteractor
import com.ferelin.domain.interactors.searchRequests.SearchRequestsInteractor
import com.ferelin.feature_search.adapter.createTickerAdapter
import com.ferelin.feature_search.mapper.SearchRequestMapper
import com.ferelin.feature_search.viewData.SearchViewData
import com.ferelin.navigation.Router
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.timerTask

typealias SearchLoadState = LoadState<List<SearchViewData>>

class SearchViewModel @Inject constructor(
    private val mSearchRequestsInteractor: SearchRequestsInteractor,
    private val mSearchRequestMapper: SearchRequestMapper,
    stockMapper: StockMapper,
    router: Router,
    companiesInteractor: CompaniesInteractor,
    stockPriceInteractor: StockPriceInteractor,
    dispatchersProvider: DispatchersProvider
) : BaseStocksViewModel(
    stockMapper,
    router,
    companiesInteractor,
    stockPriceInteractor,
    dispatchersProvider
) {
    private companion object {
        const val sSearchTaskTimeout = 300L
        const val sMinRequestResults = 5
    }

    val searchRequestsAdapter: BaseRecyclerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        BaseRecyclerAdapter(
            createTickerAdapter(this::onTickerClick)
        )
    }

    val popularSearchRequestsAdapter: BaseRecyclerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        BaseRecyclerAdapter(
            createTickerAdapter(this::onTickerClick)
        )
    }

    private val mSearchRequestsState = MutableStateFlow<SearchLoadState>(LoadState.None())
    val searchRequestsState: StateFlow<SearchLoadState>
        get() = mSearchRequestsState.asStateFlow()

    private val mPopularSearchRequestsState = MutableStateFlow<SearchLoadState>(LoadState.None())
    val popularSearchRequestsState: StateFlow<SearchLoadState>
        get() = mPopularSearchRequestsState.asStateFlow()

    private val mSearchStocksResult = MutableStateFlow<List<StockViewData>>(emptyList())
    val searchStocksResult: StateFlow<List<StockViewData>>
        get() = mSearchStocksResult.asStateFlow()

    private var mSearchTaskTimer: Timer? = null
    private var mLastSearchRequest = ""

    fun loadSearchRequests() {
        viewModelScope.launch(mDispatchesProvider.IO) {
            mSearchRequestsState.value = LoadState.Loading()
            mPopularSearchRequestsState.value = LoadState.Loading()

            mSearchRequestsState.value = LoadState.Prepared(
                data = mSearchRequestsInteractor
                    .getSearchRequests()
                    .map(mSearchRequestMapper::map)
            )

            mPopularSearchRequestsState.value = LoadState.Prepared(
                data = mSearchRequestsInteractor
                    .getPopularSearchRequests()
                    .map(mSearchRequestMapper::map)
            )
        }
    }

    fun onSearchTextChanged(searchText: String) {
        if (searchText == mLastSearchRequest) {
            return
        }

        mSearchTaskTimer?.cancel()
        mSearchTaskTimer = Timer().apply {
            schedule(timerTask {
                viewModelScope.launch(mDispatchesProvider.IO) {
                    initSearch(searchText)
                }
            }, sSearchTaskTimeout)
        }
    }

    private fun onTickerClick(searchViewData: SearchViewData) {
        // init search
    }

    private suspend fun initSearch(searchText: String) {
        if (searchText.isEmpty()) {
            mSearchStocksResult.value = emptyList()
        } else {
            val searchResults = search(searchText)
            onNewSearchRequest(searchText, searchResults.size)
            mSearchStocksResult.value = searchResults
        }

        mLastSearchRequest = searchText
    }

    private fun search(searchText: String): List<StockViewData> {
        mStocksLoadState.value.ifPrepared { preparedStocksState ->
            val itemsToSearchIn = if (searchText.length > mLastSearchRequest.length) {
                mSearchStocksResult.value
            } else {
                preparedStocksState.data
            }

            return itemsToSearchIn.filter { filterCompanies(it, searchText) }
        }

        return emptyList()
    }

    private suspend fun onNewSearchRequest(searchText: String, results: Int) {
        if (results >= sMinRequestResults) {
            mSearchRequestsInteractor.cacheSearchRequest(searchText)
        }
    }

    private fun filterCompanies(item: StockViewData, searchText: String): Boolean {
        return item.name
            .lowercase(Locale.ROOT)
            .contains(searchText.lowercase(Locale.ROOT))
                || item.ticker
            .lowercase(Locale.ROOT)
            .contains(searchText.lowercase(Locale.ROOT))
    }
}