package com.ferelin.features.about.ui.chart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.ui.params.ChartParams
import com.ferelin.core.ui.view.BaseFragment
import com.ferelin.core.ui.view.custom.chart.ChartPastPrices
import com.ferelin.core.ui.view.launchAndRepeatWithViewLifecycle
import com.ferelin.core.ui.viewData.StockPriceViewData
import com.ferelin.features.about.databinding.FragmentChartBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal class ChartFragment : BaseFragment<FragmentChartBinding>() {
  override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentChartBinding
    get() = FragmentChartBinding::inflate

  @Inject
  lateinit var viewModelFactory: ChartViewModelFactory.Factory
  private val viewModel: ChartViewModel by viewModels {
    val params = requireArguments()[CHART_SCREEN_KEY] as ChartParams
    viewModelFactory.create(params)
  }

  override fun initUx() {
    with(viewBinding) {
      cardViewDay.setOnClickListener { viewModel.setChartMode(ChartViewMode.Days) }
      cardViewWeek.setOnClickListener { viewModel.setChartMode(ChartViewMode.Weeks) }
      cardViewMonth.setOnClickListener { viewModel.setChartMode(ChartViewMode.Months) }
      cardViewHalfYear.setOnClickListener { viewModel.setChartMode(ChartViewMode.SixMonths) }
      cardViewYear.setOnClickListener { viewModel.setChartMode(ChartViewMode.Year) }
      cardViewAll.setOnClickListener { viewModel.setChartMode(ChartViewMode.Days) }

    }
  }

  override fun initObservers() {
    with(viewModel) {
      launchAndRepeatWithViewLifecycle {
        pastPrices
          .combine(
            flow = chartMode,
            transform = { pastPrices, chartMode ->
              ChartMapper.mapByViewMode(chartMode, pastPrices)
            }
          )
          .filterNotNull()
          .flowOn(Dispatchers.Main)
          .onEach(this@ChartFragment::onPastPrices)
          .launchIn(this)

        pastPricesLce
          .flowOn(Dispatchers.Main)
          .onEach(this@ChartFragment::onPastPricesLce)
          .launchIn(this)

        stockPrice
          .flowOn(Dispatchers.Main)
          .onEach(this@ChartFragment::onStockPrice)
          .launchIn(this)

        stockPriceLce
          .flowOn(Dispatchers.Main)
          .onEach(this@ChartFragment::onStockPriceLce)
          .launchIn(this)

        networkState
          .flowOn(Dispatchers.Main)
          .onEach { /*TODO*/ }
          .launchIn(this)

        chartMode
          .flowOn(Dispatchers.Main)
          .onEach { /*TODO*/ }
          .launchIn(this)
      }
    }
  }

  private fun onPastPrices(pastPrices: ChartPastPrices) {
    viewBinding.chartView.setData(pastPrices)
  }

  private fun onPastPricesLce(lceState: LceState) {
    // TODO
    when (lceState) {
      is LceState.Content -> Unit
      is LceState.Loading -> Unit
      is LceState.Error -> Unit
      else -> Unit
    }
  }

  private fun onStockPrice(stockPrice: StockPriceViewData) {
    viewBinding.textViewCurrentPrice.text = stockPrice.price
    viewBinding.textViewDayProfit.text = stockPrice.profit
  }

  private fun onStockPriceLce(lceState: LceState) {
    // TODO
    when (lceState) {
      is LceState.Content -> Unit
      is LceState.Loading -> Unit
      is LceState.Error -> Unit
      else -> Unit
    }
  }
}