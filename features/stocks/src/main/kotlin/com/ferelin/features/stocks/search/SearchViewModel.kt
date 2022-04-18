package com.ferelin.features.stocks.search

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.SearchRequestsUseCase
import com.ferelin.features.stocks.stocks.StockViewData
import com.ferelin.features.stocks.stocks.toStockViewData
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import kotlin.concurrent.timerTask

@Immutable
internal data class SearchUiState(
    val inputSearchRequest: String = "",
    val searchResults: List<StockViewData> = emptyList(),
    val searchResultsLce: LceState = LceState.None,
    val searchRequests: List<SearchViewData> = emptyList(),
    val searchRequestsLce: LceState = LceState.None,
    val popularSearchRequests: List<SearchViewData> = emptyList(),
    val popularSearchRequestsLce: LceState = LceState.None
)

internal class SearchViewModel(
    private val searchRequestsUseCase: SearchRequestsUseCase,
    private val dispatchersProvider: DispatchersProvider,
    companyUseCase: CompanyUseCase
) : ViewModel() {
    private val viewModelState = MutableStateFlow(SearchUiState())
    val uiState = viewModelState.asStateFlow()

    private val searchRequest = MutableStateFlow("")
    private var searchTaskTimer: Timer? = null

    init {
        searchRequest
            .combine(
                flow = companyUseCase.companies,
                transform = { searchRequest, stocks ->
                    val stocksViewData = stocks.map { it.toStockViewData() }
                    searchRequest to stocksViewData
                }
            )
            .flowOn(dispatchersProvider.IO)
            .onEach(this::doSearch)
            .launchIn(viewModelScope)

        searchRequestsUseCase.searchRequests
            .map { requests -> requests.map { it.toSearchViewData() } }
            .flowOn(dispatchersProvider.IO)
            .onEach(this::onSearchRequests)
            .launchIn(viewModelScope)

        searchRequestsUseCase.searchRequestsLce
            .onEach(this::onSearchRequestsLce)
            .launchIn(viewModelScope)

        searchRequestsUseCase.popularSearchRequests
            .map { requests -> requests.map { it.toSearchViewData() } }
            .flowOn(dispatchersProvider.IO)
            .onEach(this::onPopularSearchRequests)
            .launchIn(viewModelScope)

        searchRequestsUseCase.popularSearchRequestsLce
            .onEach(this::onPopularSearchRequestsLce)
            .launchIn(viewModelScope)
    }

    fun onSearchTextChanged(searchText: String) {
        searchRequest.value = searchText
        viewModelState.update { it.copy(inputSearchRequest = searchText) }
    }

    fun onTickerClick(searchViewData: SearchViewData) {
        onSearchTextChanged(searchViewData.text)
    }

    private fun onSearchRequests(searchRequests: List<SearchViewData>) {
        viewModelState.update { it.copy(searchRequests = searchRequests) }
    }

    private fun onSearchRequestsLce(lceState: LceState) {
        viewModelState.update { it.copy(searchRequestsLce = lceState) }
    }

    private fun onPopularSearchRequests(searchRequests: List<SearchViewData>) {
        viewModelState.update { it.copy(popularSearchRequests = searchRequests) }
    }

    private fun onPopularSearchRequestsLce(lceState: LceState) {
        viewModelState.update { it.copy(popularSearchRequestsLce = lceState) }
    }

    private fun doSearch(requestWithStocks: Pair<String, List<StockViewData>>) {
        searchTaskTimer?.cancel()

        val searchText = requestWithStocks.first
        val stocks = requestWithStocks.second

        if (searchText.isEmpty()) {
            resetSearchState()
        } else {
            initSearch(stocks, searchText)
        }
    }

    private fun initSearch(stocks: List<StockViewData>, searchText: String) {
        searchTaskTimer = Timer().apply {
            viewModelState.update { it.copy(searchResultsLce = LceState.Loading) }

            schedule(timerTask {
                viewModelScope.launch(dispatchersProvider.IO) {
                    val results = stocks.filterBySearch(searchText)
                    updateSearchState(results)
                    searchRequestsUseCase.onNewSearchRequest(searchText, results.size)
                }
            }, SEARCH_TASK_TIMEOUT)
        }
    }

    private fun resetSearchState() {
        viewModelState.update {
            it.copy(
                searchResults = emptyList(),
                searchResultsLce = LceState.None
            )
        }
    }

    private fun updateSearchState(results: List<StockViewData>) {
        viewModelState.update {
            it.copy(
                searchResults = results,
                searchResultsLce = LceState.Content
            )
        }
    }
}

private const val SEARCH_TASK_TIMEOUT = 350L

private fun List<StockViewData>.filterBySearch(searchText: String): List<StockViewData> {
    return this.filter { item ->
        item.name.lowercase(Locale.ROOT)
            .contains(searchText.lowercase(Locale.ROOT)) || item.ticker.lowercase(Locale.ROOT)
            .contains(searchText.lowercase(Locale.ROOT))
    }
}