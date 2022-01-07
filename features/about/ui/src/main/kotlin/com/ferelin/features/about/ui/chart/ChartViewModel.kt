package com.ferelin.features.about.ui.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ferelin.core.domain.entities.StockPriceUseCase
import com.ferelin.core.ui.mapper.StockPriceMapper
import com.ferelin.core.network.NetworkListener
import com.ferelin.core.ui.params.ChartParams
import com.ferelin.features.about.domain.PastPricesUseCase
import kotlinx.coroutines.flow.*
import javax.inject.Inject

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

internal class ChartViewModelFactory @Inject constructor(
  var chartParams: ChartParams? = null,
  private val networkListener: NetworkListener,
  private val pastPricesUseCase: PastPricesUseCase,
  private val stockPricesUseCase: StockPriceUseCase,
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return ChartViewModel(
      chartParams!!,
      networkListener,
      pastPricesUseCase,
      stockPricesUseCase
    ) as T
  }
}