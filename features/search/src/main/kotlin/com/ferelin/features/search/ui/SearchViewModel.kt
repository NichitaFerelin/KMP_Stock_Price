package com.ferelin.features.search.ui

import androidx.lifecycle.viewModelScope
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.core.domain.usecase.StockPriceUseCase
import com.ferelin.core.ui.view.routing.Coordinator
import com.ferelin.core.ui.view.adapter.BaseRecyclerAdapter
import com.ferelin.core.ui.viewData.StockViewData
import com.ferelin.core.ui.viewData.utils.StockStyleProvider
import com.ferelin.core.ui.viewModel.StocksViewModel
import com.ferelin.core.domain.usecase.SearchRequestsUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE
import kotlin.concurrent.timerTask

internal class SearchViewModel @Inject constructor(
  private val searchRequestsUseCase: SearchRequestsUseCase,
  private val coordinator: Coordinator,
  favouriteCompanyUseCase: FavouriteCompanyUseCase,
  stockPriceUseCase: StockPriceUseCase,
  stockStyleProvider: StockStyleProvider,
  companyUseCase: CompanyUseCase,
) : StocksViewModel(
  favouriteCompanyUseCase,
  stockPriceUseCase,
  stockStyleProvider,
  companyUseCase
) {
  private val _searchResults = MutableStateFlow<List<StockViewData>>(emptyList())
  val searchResults: Flow<List<StockViewData>> = _searchResults.asStateFlow()

  private val searchRequest = MutableStateFlow("")
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
    searchRequest
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
    viewModelScope.launch {

    }
  }

  fun onBack() {
    // navigate
  }

  private fun onTickerClick(searchViewData: SearchViewData) {
    // navigate
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
        viewModelScope.launch {
          _searchResults.value = stocks.filterBySearch(searchText)
            .also { results ->
              if (results.size >= SearchRequestsUseCase.REQUIRED_RESULTS_FOR_CACHE) {
                searchRequestsUseCase.add(searchText)
              }
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