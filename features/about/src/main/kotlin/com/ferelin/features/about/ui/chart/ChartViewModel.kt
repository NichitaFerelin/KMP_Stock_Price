package com.ferelin.features.about.ui.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ferelin.core.domain.usecase.PastPricesUseCase
import com.ferelin.core.domain.usecase.StockPriceUseCase
import com.ferelin.core.network.NetworkListener
import com.ferelin.core.ui.mapper.StockPriceMapper
import com.ferelin.core.ui.params.ChartParams
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*

internal class ChartViewModel(
  private val chartParams: ChartParams,
  networkListener: NetworkListener,
  pastPricesUseCase: PastPricesUseCase,
  stockPricesUseCase: StockPriceUseCase,
) : ViewModel() {

  val networkState = networkListener.networkState
    .distinctUntilChanged()
    .onEach {
      stockPricesUseCase.fetchPrice(chartParams.companyId, chartParams.companyTicker)
      pastPricesUseCase.fetchPastPrices(chartParams.companyId)
    }

  val pastPricesLce = pastPricesUseCase.pastPricesLce
  val pastPrices = pastPricesUseCase.getAllBy(chartParams.companyId)
    .map { it.map(ChartMapper::map) }

  val stockPriceLce = stockPricesUseCase.stockPriceLce
  val stockPrice = stockPricesUseCase.stockPrice
    .map { it.find { stockPrice -> stockPrice.id == chartParams.companyId } }
    .filterNotNull()
    .map(StockPriceMapper::map)

  private val chartModeState = MutableStateFlow(ChartViewMode.All)
  val chartMode: Flow<ChartViewMode> = chartModeState.asStateFlow()

  fun setChartMode(chartViewMode: ChartViewMode) {
    chartModeState.value = chartViewMode
  }
}

internal class ChartViewModelFactory @AssistedInject constructor(
  @Assisted(CHART_PARAMS) private val chartParams: ChartParams,
  private val networkListener: NetworkListener,
  private val pastPricesUseCase: PastPricesUseCase,
  private val stockPricesUseCase: StockPriceUseCase,
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    require(modelClass == ChartViewModel::class)
    return ChartViewModel(chartParams, networkListener, pastPricesUseCase, stockPricesUseCase) as T
  }

  @AssistedFactory
  interface Factory {
    fun create(@Assisted(CHART_PARAMS) chartParams: ChartParams): ChartViewModelFactory
  }
}

internal const val CHART_PARAMS = "chart-params"