package com.ferelin.features.about.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.usecase.PastPricesUseCase
import com.ferelin.core.domain.usecase.StockPriceUseCase
import com.ferelin.core.network.NetworkListener
import com.ferelin.core.ui.mapper.StockPriceMapper
import com.ferelin.core.ui.params.ChartParams
import com.ferelin.core.ui.viewData.StockPriceViewData
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

internal data class ChartScreenStateUi(
  val stockPrice: String = "",
  val stockPriceLce: LceState = LceState.None,
  val stockProfit: String = "",
  val priceHistory: ChartPastPrices = ChartPastPrices(),
  val priceHistoryLce: LceState = LceState.None,
  val selectedChartMode: ChartViewMode = ChartViewMode.All,
  val showNetworkError: Boolean = false
)

internal class ChartViewModel(
  private val chartParams: ChartParams,
  dispatchersProvider: DispatchersProvider,
  networkListener: NetworkListener,
  private val pastPricesUseCase: PastPricesUseCase,
  private val stockPricesUseCase: StockPriceUseCase,
) : ViewModel() {
  private val viewModelState = MutableStateFlow(ChartScreenStateUi())
  val uiState = viewModelState.asStateFlow()

  private val pastPrices = pastPricesUseCase.getAllBy(chartParams.companyId)
    .map { it.map(ChartMapper::map) }

  init {
    pastPrices
      .onEach(this::onPastPrices)
      .launchIn(viewModelScope)

    pastPricesUseCase.pastPricesLce
      .onEach(this::onPastPricesLce)
      .launchIn(viewModelScope)

    stockPricesUseCase.stockPrice
      .map { it.find { stockPrice -> stockPrice.id == chartParams.companyId } }
      .filterNotNull()
      .map(StockPriceMapper::map)
      .onEach(this::onStockPrice)
      .launchIn(viewModelScope)

    stockPricesUseCase.stockPriceLce
      .onEach(this::onStockPriceLce)
      .launchIn(viewModelScope)

    networkListener.networkState
      .distinctUntilChanged()
      .onEach(this::onNetwork)
      .flowOn(dispatchersProvider.IO)
      .launchIn(viewModelScope)
  }

  fun onChartModeSelected(chartViewMode: ChartViewMode) {
    viewModelScope.launch {
      val pastPrices = pastPrices.firstOrNull() ?: emptyList()
      viewModelState.update {
        it.copy(
          selectedChartMode = chartViewMode,
          priceHistory = ChartMapper.mapByViewMode(chartViewMode, pastPrices) ?: ChartPastPrices()
        )
      }
    }
  }

  private fun onPastPrices(pastPrices: List<PastPriceViewData>) {
    viewModelScope.launch {
      viewModelState.update {
        it.copy(
          priceHistory = ChartMapper.mapByViewMode(
            viewMode = viewModelState.value.selectedChartMode,
            pastPrices = pastPrices
          ) ?: ChartPastPrices()
        )
      }
    }
  }

  private fun onPastPricesLce(lceState: LceState) {
    viewModelState.update { it.copy(priceHistoryLce = lceState) }
  }

  private fun onStockPrice(stockPriceViewData: StockPriceViewData) {
    viewModelState.update {
      it.copy(
        stockPrice = stockPriceViewData.price,
        stockProfit = stockPriceViewData.profit
      )
    }
  }

  private fun onStockPriceLce(lceState: LceState) {
    viewModelState.update { it.copy(stockPriceLce = lceState) }
  }

  private suspend fun onNetwork(available: Boolean) {
    viewModelState.update { it.copy(showNetworkError = !available) }

    if (available) {
      stockPricesUseCase.fetchPrice(chartParams.companyId, chartParams.companyTicker)
      pastPricesUseCase.fetchPastPrices(chartParams.companyId, chartParams.companyTicker)
    }
  }
}

internal class ChartViewModelFactory @Inject constructor(
  private val chartParams: ChartParams,
  private val networkListener: NetworkListener,
  private val pastPricesUseCase: PastPricesUseCase,
  private val stockPricesUseCase: StockPriceUseCase,
  private val dispatchersProvider: DispatchersProvider
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    require(modelClass == ChartViewModel::class.java)
    return ChartViewModel(
      chartParams,
      dispatchersProvider,
      networkListener,
      pastPricesUseCase,
      stockPricesUseCase
    ) as T
  }
}