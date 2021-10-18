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
import com.ferelin.core.mapper.StockMapper
import com.ferelin.core.utils.SHARING_STOP_TIMEOUT
import com.ferelin.core.utils.StockStyleProvider
import com.ferelin.core.viewData.StockViewData
import com.ferelin.core.viewModel.BaseStocksViewModel
import com.ferelin.domain.entities.SearchRequest
import com.ferelin.domain.interactors.StockPriceInteractor
import com.ferelin.domain.interactors.companies.CompaniesInteractor
import com.ferelin.domain.interactors.searchRequests.SearchRequestsInteractor
import com.ferelin.feature_search.adapter.createTickerAdapter
import com.ferelin.feature_search.mapper.SearchRequestMapper
import com.ferelin.feature_search.view.SearchFragment
import com.ferelin.feature_search.viewData.SearchViewData
import com.ferelin.navigation.Router
import com.ferelin.shared.DispatchersProvider
import com.ferelin.shared.LoadState
import com.ferelin.shared.ifPrepared
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.timerTask

typealias SearchLoadState = LoadState<List<SearchViewData>>

class SearchViewModel @Inject constructor(
    private val mSearchRequestsInteractor: SearchRequestsInteractor,
    private val mSearchRequestMapper: SearchRequestMapper,
    private val mRouter: Router,
    companiesInteractor: CompaniesInteractor,
    stockPriceInteractor: StockPriceInteractor,
    router: Router,
    stockMapper: StockMapper,
    stockStyleProvider: StockStyleProvider,
    dispatchersProvider: DispatchersProvider
) : BaseStocksViewModel(
    stockMapper,
    dispatchersProvider,
    companiesInteractor,
    stockPriceInteractor,
    stockStyleProvider,
    router
) {
    private companion object {
        const val sSearchTaskTimeout = 350L
        const val sMaxRequestResults = 5
    }

    val searchRequestsState: StateFlow<LoadState<List<SearchRequest>>>
            by lazy(LazyThreadSafetyMode.NONE) {
                mSearchRequestsInteractor
                    .searchRequestsState
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

    private val mPopularSearchRequestsState = MutableStateFlow<SearchLoadState>(LoadState.None())

    private val mOnNewSearchText = MutableSharedFlow<String>()
    val searchTextChanged: SharedFlow<String>
        get() = mOnNewSearchText.asSharedFlow()

    private val mSearchResultsExists = MutableStateFlow(false)
    val searchResultsExists: StateFlow<Boolean>
        get() = mSearchResultsExists.asStateFlow()

    private var mSearchTaskTimer: Timer? = null
    private var mLastSearchRequest = ""

    var transitionState = SearchFragment.TRANSITION_START

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

    fun loadSearchRequests() {
        viewModelScope.launch(mDispatchesProvider.IO) {
            mPopularSearchRequestsState.value = LoadState.Loading()

            launch {
                val dbSearchRequests = mSearchRequestsInteractor.getSearchRequests()
                onSearchRequestsChanged(dbSearchRequests)
            }
            launch {
                val dbPopularSearchRequests = mSearchRequestsInteractor.getPopularSearchRequests()
                onPopularSearchRequestsChanged(dbPopularSearchRequests)
            }
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

    fun onBackClick() {
        mRouter.back()
    }

    private fun onTickerClick(searchViewData: SearchViewData) {
        viewModelScope.launch(mDispatchesProvider.IO) {
            mOnNewSearchText.emit(searchViewData.text)
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

        mSearchResultsExists.value = searchResults.isNotEmpty()

        withContext(mDispatchesProvider.Main) {
            stocksAdapter.setData(searchResults)
        }

        mLastSearchRequest = text
    }

    private fun search(searchText: String): List<StockViewData> {
        return mStocksLoadState.value.ifPrepared { preparedStocksState ->
            preparedStocksState
                .data
                .filter { filterCompanies(it, searchText) }
        } ?: emptyList()
    }

    private suspend fun onSearchRequestsChanged(searchRequests: List<SearchRequest>) {
        val mappedRequests = searchRequests.map(mSearchRequestMapper::map)

        withContext(mDispatchesProvider.Main) {
            searchRequestsAdapter.setData(mappedRequests)
        }
    }

    private suspend fun onPopularSearchRequestsChanged(searchRequests: List<SearchRequest>) {
        val mappedRequests = searchRequests.map(mSearchRequestMapper::map)
        mPopularSearchRequestsState.value = LoadState.Prepared(mappedRequests)

        withContext(mDispatchesProvider.Main) {
            popularSearchRequestsAdapter.setData(mappedRequests)
        }
    }

    private suspend fun onNewSearchRequest(text: String, results: Int) {
        if (results in 1..sMaxRequestResults) {
            mSearchRequestsInteractor.cacheSearchRequest(text, true)
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