package com.ferelin.features.search

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.core.domain.usecase.SearchRequestsUseCase
import com.ferelin.core.ui.viewData.StockViewData
import com.ferelin.core.ui.viewModel.BaseStocksViewModel
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import kotlin.concurrent.timerTask

@Immutable
internal data class SearchStateUi(
  val searchResults: List<StockViewData> = emptyList(),
  val searchResultsLce: LceState = LceState.None,
  val showCloseIcon: Boolean = false,
  val inputSearchRequest: String = "",
  val searchRequests: List<SearchViewData> = emptyList(),
  val searchRequestsLce: LceState = LceState.None,
  val popularSearchRequests: List<SearchViewData> = emptyList(),
  val popularSearchRequestsLce: LceState = LceState.None
)

internal class SearchViewModel(
  private val searchRequestsUseCase: SearchRequestsUseCase,
  favouriteCompanyUseCase: FavouriteCompanyUseCase,
  companyUseCase: CompanyUseCase,
  dispatchersProvider: DispatchersProvider
) : BaseStocksViewModel(
  favouriteCompanyUseCase,
  dispatchersProvider,
  companyUseCase
) {
  private val viewModelState = MutableStateFlow(SearchStateUi())
  val uiState = viewModelState.asStateFlow()

  private val searchRequest = MutableStateFlow("")
  private var searchTaskTimer: Timer? = null

  init {
    searchRequestsUseCase.searchRequests
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.io())
      .map { requests -> requests.map(SearchRequestMapper::map) }
      .subscribe(this::onSearchRequests) { Timber.e(it) }

    searchRequestsUseCase.searchRequestsLce
      .onEach(this::onSearchRequestsLce)
      .launchIn(viewModelScope)

    searchRequestsUseCase.popularSearchRequests
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.io())
      .map { requests -> requests.map(SearchRequestMapper::map) }
      .subscribe(this::onPopularSearchRequests) { Timber.e(it) }

    searchRequestsUseCase.popularSearchRequestsLce
      .onEach(this::onPopularSearchRequestsLce)
      .launchIn(viewModelScope)
  }

  override fun onCleared() {
    searchTaskTimer?.cancel()
    super.onCleared()
  }

  fun onSearchTextChanged(searchText: String) {
    viewModelState.update { it.copy(showCloseIcon = searchText.isNotEmpty()) }
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

    viewModelState.update { it.copy(searchResultsLce = LceState.None) }
    searchTaskTimer?.cancel()

    if (searchText.isEmpty()) {
      viewModelState.update {
        it.copy(
          searchResults = emptyList(),
          searchResultsLce = LceState.Content
        )
      }
      return
    }
    searchTaskTimer = Timer().apply {
      viewModelState.update { it.copy(searchResultsLce = LceState.Loading) }
      schedule(timerTask {
        viewModelScope.launch(dispatchersProvider.IO) {
          val results = stocks.filterBySearch(searchText)
          viewModelState.update {
            it.copy(
              searchResults = results,
              searchResultsLce = LceState.Content
            )
          }
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

internal class SearchViewModelFactory @Inject constructor(
  private val searchRequestsUseCase: SearchRequestsUseCase,
  private val favouriteCompanyUseCase: FavouriteCompanyUseCase,
  private val companyUseCase: CompanyUseCase,
  private val dispatchersProvider: DispatchersProvider
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    require(modelClass == SearchViewModel::class.java)
    return SearchViewModel(
      searchRequestsUseCase,
      favouriteCompanyUseCase,
      companyUseCase,
      dispatchersProvider
    ) as T
  }
}