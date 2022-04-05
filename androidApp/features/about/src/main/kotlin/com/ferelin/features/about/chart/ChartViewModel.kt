package com.ferelin.features.about.chart

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.network.NetworkListener
import com.ferelin.core.ui.mapper.StockPriceMapper
import com.ferelin.core.ui.params.ChartParams
import com.ferelin.core.ui.viewData.StockPriceViewData
import com.ferelin.stockprice.domain.entity.CompanyId
import com.ferelin.stockprice.domain.entity.LceState
import com.ferelin.common.domain.usecase.PastPricesUseCase
import com.ferelin.common.domain.usecase.StockPriceUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Immutable
internal data class ChartScreenStateUi(
  val stockPrice: String = "",
  val stockPriceLce: LceState = LceState.None,
  val stockProfit: String = "",
  val priceHistory: Candles = Candles(),
  val priceHistoryLce: LceState = LceState.None,
  val selectedChartMode: ChartViewMode = ChartViewMode.All,
  val showNetworkError: Boolean = false
)

internal class ChartViewModel(
  private val chartParams: ChartParams,
  private val pastPricesUseCase: PastPricesUseCase,
  private val stockPricesUseCase: StockPriceUseCase,
  dispatchersProvider: DispatchersProvider,
  networkListener: NetworkListener,
) : ViewModel() {
  private val viewModelState = MutableStateFlow(ChartScreenStateUi())
  val uiState = viewModelState.asStateFlow()

  private val pastPrices = pastPricesUseCase
    .getAllBy(companyId = CompanyId(chartParams.companyId))
    .map { it.map(ChartMapper::map) }

  init {
    pastPrices
      .onEach(this::onPastPrices)
      .launchIn(viewModelScope)

    pastPricesUseCase.pastPricesLce
      .onEach(this::onPastPricesLce)
      .launchIn(viewModelScope)

    stockPricesUseCase.stockPrice
      .map { it.find { stockPrice -> stockPrice.id.value == chartParams.companyId } }
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
          priceHistory = ChartMapper.mapByViewMode(chartViewMode, pastPrices) ?: Candles()
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
          ) ?: Candles()
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
      val companyId = CompanyId(chartParams.companyId)
      stockPricesUseCase.fetchPrice(companyId, chartParams.companyTicker)
      pastPricesUseCase.fetchPastPrices(companyId, chartParams.companyTicker)
    }
  }
}