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
import com.ferelin.core.adapter.base.BaseRecyclerAdapter
import com.ferelin.core.mapper.CompanyWithStockPriceMapper
import com.ferelin.core.mapper.StockPriceMapper
import com.ferelin.core.utils.SHARING_STOP_TIMEOUT
import com.ferelin.core.utils.StockStyleProvider
import com.ferelin.core.viewData.StockViewData
import com.ferelin.core.viewModel.BaseStocksViewModel
import com.ferelin.core.viewModel.StocksMode
import com.ferelin.domain.interactors.StockPriceInteractor
import com.ferelin.domain.interactors.companies.CompaniesInteractor
import com.ferelin.domain.interactors.searchRequests.SearchRequestsInteractor
import com.ferelin.feature_search.adapter.createTickerAdapter
import com.ferelin.feature_search.mapper.SearchRequestMapper
import com.ferelin.feature_search.view.SearchFragment
import com.ferelin.feature_search.viewData.SearchViewData
import com.ferelin.navigation.Router
import com.ferelin.shared.LoadState
import com.ferelin.shared.ifPrepared
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.timerTask

class SearchViewModel @Inject constructor(
    private val searchRequestsInteractor: SearchRequestsInteractor,
    private val searchRequestMapper: SearchRequestMapper,
    companiesInteractor: CompaniesInteractor,
    stockPriceInteractor: StockPriceInteractor,
    router: Router,
    companyWithStockPriceMapper: CompanyWithStockPriceMapper,
    stockPriceMapper: StockPriceMapper,
    stockStyleProvider: StockStyleProvider,
) : BaseStocksViewModel(
    companyWithStockPriceMapper,
    router,
    companiesInteractor,
    stockPriceInteractor,
    stockStyleProvider,
    stockPriceMapper,
    StocksMode.ALL
) {
    private val mPopularSearchRequestsState =
        MutableStateFlow<LoadState<List<SearchViewData>>>(LoadState.None())

    private val _onNewSearchText = MutableSharedFlow<String>()
    val searchTextChanged: SharedFlow<String> = _onNewSearchText.asSharedFlow()

    private val _searchResultsExists = MutableStateFlow(false)
    val searchResultsExists: StateFlow<Boolean> = _searchResultsExists.asStateFlow()

    private var searchTaskTimer: Timer? = null
    private var lastSearchRequest = ""

    var transitionState = SearchFragment.TRANSITION_START

    val searchRequestsState: StateFlow<LoadState<Set<String>>> by lazy(LazyThreadSafetyMode.NONE) {
        searchRequestsInteractor.searchRequestsState
            .onEach {
                if (it is LoadState.Prepared) {
                    onSearchRequestsChanged(it.data)
                }
            }
            .stateIn(
                viewModelScope,
                SharingStarted.WhileSubscribed(SHARING_STOP_TIMEOUT),
                LoadState.None()
            )
    }

    val searchRequestsAdapter: BaseRecyclerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        BaseRecyclerAdapter(
            createTickerAdapter(this::onTickerClick)
        ).apply { setHasStableIds(true) }
    }

    val popularSearchRequestsAdapter: BaseRecyclerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        BaseRecyclerAdapter(
            createTickerAdapter(this::onTickerClick)
        ).apply { setHasStableIds(true) }
    }

    private companion object {
        const val SEARCH_TASK_TIMEOUT = 350L
        const val MAX_REQUESTS_RESULT = 5
    }

    fun loadSearchRequests() {
        viewModelScope.launch {
            mPopularSearchRequestsState.value = LoadState.Loading()

            launch {
                val dbSearchRequests = searchRequestsInteractor.getAll()
                onSearchRequestsChanged(dbSearchRequests)
            }
            launch {
                val dbPopularSearchRequests = searchRequestsInteractor.getAllPopular()
                onPopularSearchRequestsChanged(dbPopularSearchRequests)
            }
        }
    }

    fun onSearchTextChanged(searchText: String) {
        if (searchText == lastSearchRequest) {
            return
        }
        searchTaskTimer?.cancel()
        searchTaskTimer = Timer().apply {
            schedule(timerTask {
                viewModelScope.launch {
                    initSearch(searchText)
                }
            }, SEARCH_TASK_TIMEOUT)
        }
    }

    fun onBackClick() {
        router.back()
    }

    private fun onTickerClick(searchViewData: SearchViewData) {
        viewModelScope.launch {
            _onNewSearchText.emit(searchViewData.text)
        }
    }

    private suspend fun initSearch(text: String) {
        val searchResults = if (text.isEmpty()) {
            emptyList()
        } else {
            search(text).also { searchResults ->
                onNewSearchRequest(text, searchResults.size)
            }
        }

        this._searchResultsExists.value = searchResults.isNotEmpty()

        withContext(Dispatchers.Main) {
            stocksAdapter.setData(searchResults)
        }

        lastSearchRequest = text
    }

    private fun search(searchText: String): List<StockViewData> {
        return stockLoadState.value.ifPrepared { preparedStocksState ->
            preparedStocksState
                .data
                .filter { filterCompanies(it, searchText) }
        } ?: emptyList()
    }

    private suspend fun onSearchRequestsChanged(searchRequests: Set<String>) {
        val mappedRequests = searchRequestMapper.map(searchRequests).reversed()

        withContext(Dispatchers.Main) {
            searchRequestsAdapter.setData(mappedRequests)
        }
    }

    private suspend fun onPopularSearchRequestsChanged(searchRequests: Set<String>) {
        val mappedRequests = searchRequestMapper.map(searchRequests)
        mPopularSearchRequestsState.value = LoadState.Prepared(mappedRequests)

        withContext(Dispatchers.Main) {
            popularSearchRequestsAdapter.setData(mappedRequests)
        }
    }

    private suspend fun onNewSearchRequest(text: String, results: Int) {
        if (results in 1..MAX_REQUESTS_RESULT) {
            searchRequestsInteractor.cache(text)
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