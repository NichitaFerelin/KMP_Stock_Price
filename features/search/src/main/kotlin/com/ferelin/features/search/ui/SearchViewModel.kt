package com.ferelin.features.search.ui

import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.core.domain.usecase.SearchRequestsUseCase
import com.ferelin.core.ui.view.adapter.BaseRecyclerAdapter
import com.ferelin.core.ui.view.routing.Coordinator
import com.ferelin.core.ui.viewData.StockViewData
import com.ferelin.core.ui.viewData.utils.StockStyleProvider
import com.ferelin.core.ui.viewModel.BaseStocksViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.concurrent.timerTask

internal class SearchViewModel @Inject constructor(
  private val searchRequestsUseCase: SearchRequestsUseCase,
  private val coordinator: Coordinator,
  stockStyleProvider: StockStyleProvider,
  favouriteCompanyUseCase: FavouriteCompanyUseCase,
  companyUseCase: CompanyUseCase,
  dispatchersProvider: DispatchersProvider
) : BaseStocksViewModel(
  companyUseCase,
  favouriteCompanyUseCase,
  stockStyleProvider,
  dispatchersProvider
) {
  private val _searchResults = MutableStateFlow<List<StockViewData>>(emptyList())
  val searchResults: Flow<List<StockViewData>> = _searchResults.asStateFlow()

  private val _searchRequest = MutableStateFlow("")
  val searchRequest: Flow<String> = _searchRequest.asStateFlow()

  private var searchTaskTimer: Timer? = null

  val searchRequests = searchRequestsUseCase.searchRequests
    .map { requests -> requests.map(SearchRequestMapper::map) }
  val searchRequestsLce = searchRequestsUseCase.searchRequestsLce

  val popularSearchRequests = searchRequestsUseCase.popularSearchRequests
    .map { requests -> requests.map(SearchRequestMapper::map) }
  val popularSearchRequestsLce = searchRequestsUseCase.popularSearchRequestsLce

  val searchRequestsAdapter: BaseRecyclerAdapter by lazy(NONE) {
    BaseRecyclerAdapter(
      createTickerAdapter(this::onTickerClick)
    ).apply { setHasStableIds(true) }
  }
  val popularSearchRequestsAdapter: BaseRecyclerAdapter by lazy(NONE) {
    BaseRecyclerAdapter(
      createTickerAdapter(this::onTickerClick)
    ).apply { setHasStableIds(true) }
  }

  init {
    _searchRequest
      .combine(
        flow = companies,
        transform = { searchRequest, stocks -> searchRequest to stocks }
      )
      .onEach(this::onSearchRequest)
      .launchIn(viewModelScope)
  }

  override fun onCleared() {
    searchTaskTimer?.cancel()
    super.onCleared()
  }

  fun onSearchTextChanged(searchText: String) {
    viewModelScope.launch(dispatchersProvider.IO) {
      _searchRequest.value = searchText
    }
  }

  fun onBack() {
    coordinator.onEvent(SearchRouteEvent.BackRequested)
  }

  private fun onTickerClick(searchViewData: SearchViewData) {
    _searchRequest.value = searchViewData.text
  }

  override fun onStockClick(stockViewData: StockViewData) {
    coordinator.onEvent(
      event = SearchRouteEvent.OpenStockInfoRequested(
        companyId = stockViewData.id,
        ticker = stockViewData.ticker,
        name = stockViewData.name
      )
    )
  }

  private fun onSearchRequest(requestWithStocks: Pair<String, List<StockViewData>>) {
    val searchText = requestWithStocks.first
    val stocks = requestWithStocks.second
    searchTaskTimer?.cancel()

    if (searchText.isEmpty()) {
      _searchResults.value = emptyList()
      return
    }
    searchTaskTimer = Timer().apply {
      schedule(timerTask {
        viewModelScope.launch(dispatchersProvider.IO) {
          _searchResults.value = stocks.filterBySearch(searchText)
            .also { results ->
              searchRequestsUseCase.onNewSearchRequest(searchText, results.size)
            }
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