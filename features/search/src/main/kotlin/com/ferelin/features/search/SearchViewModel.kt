package com.ferelin.features.search

import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.core.domain.usecase.SearchRequestsUseCase
import com.ferelin.core.ui.viewData.StockViewData
import com.ferelin.core.ui.viewModel.BaseStocksViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.timerTask

data class SearchStateUi(
  val searchResults: List<StockViewData> = emptyList(),
  val inputSearchRequest: String = "",
  val searchRequests: List<SearchViewData> = emptyList(),
  val searchRequestsLce: LceState = LceState.None,
  val popularSearchRequests: List<SearchViewData> = emptyList(),
  val popularSearchRequestsLce: LceState = LceState.None
)

class SearchViewModel @Inject constructor(
  private val searchRequestsUseCase: SearchRequestsUseCase,
  favouriteCompanyUseCase: FavouriteCompanyUseCase,
  companyUseCase: CompanyUseCase,
  dispatchersProvider: DispatchersProvider
) : BaseStocksViewModel(
  companyUseCase,
  favouriteCompanyUseCase,
  dispatchersProvider
) {
  private val viewModelState = MutableStateFlow(SearchStateUi())
  val uiState = viewModelState.asStateFlow()

  private val searchRequest = MutableStateFlow("")
  private var searchTaskTimer: Timer? = null

  init {
    searchRequest
      .combine(
        flow = companies,
        transform = { searchRequest, stocks -> searchRequest to stocks }
      )
      .onEach(this::doSearch)
      .launchIn(viewModelScope)

    searchRequestsUseCase.searchRequests
      .map { requests -> requests.map(SearchRequestMapper::map) }
      .onEach(this::onSearchRequests)
      .flowOn(dispatchersProvider.IO)
      .launchIn(viewModelScope)

    searchRequestsUseCase.searchRequestsLce
      .onEach(this::onSearchRequestsLce)
      .launchIn(viewModelScope)

    searchRequestsUseCase.popularSearchRequests
      .map { requests -> requests.map(SearchRequestMapper::map) }
      .onEach(this::onPopularSearchRequests)
      .flowOn(dispatchersProvider.IO)
      .launchIn(viewModelScope)

    searchRequestsUseCase.popularSearchRequestsLce
      .onEach(this::onPopularSearchRequestsLce)
      .launchIn(viewModelScope)
  }

  override fun onCleared() {
    searchTaskTimer?.cancel()
    super.onCleared()
  }

  fun onSearchTextChanged(searchText: String) {
    viewModelScope.launch {
      searchRequest.value = searchText
      viewModelState.update { it.copy(inputSearchRequest = searchText) }
    }
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
    val searchText = requestWithStocks.first
    val stocks = requestWithStocks.second
    searchTaskTimer?.cancel()

    if (searchText.isEmpty()) {
      viewModelState.update { it.copy(searchResults = emptyList()) }
      return
    }
    searchTaskTimer = Timer().apply {
      schedule(timerTask {
        viewModelScope.launch(dispatchersProvider.IO) {
          val results = stocks.filterBySearch(searchText)
          viewModelState.update { it.copy(searchResults = results) }
          searchRequestsUseCase.onNewSearchRequest(searchText, results.size)
        }
      }, SEARCH_TASK_TIMEOUT)
    }
  }
}

internal const val SEARCH_TASK_TIMEOUT = 350L

internal fun List<StockViewData>.filterBySearch(searchText: String): List<StockViewData> {
  return this.filter { item ->
    item.name
      .lowercase(Locale.ROOT)
      .contains(searchText.lowercase(Locale.ROOT))
      || item.ticker
      .lowercase(Locale.ROOT)
      .contains(searchText.lowercase(Locale.ROOT))
  }
}