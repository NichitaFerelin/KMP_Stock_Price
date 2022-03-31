package com.ferelin.features.about.chart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.PastPricesUseCase
import com.ferelin.core.domain.usecase.StockPriceUseCase
import com.ferelin.core.network.NetworkListener
import com.ferelin.core.ui.params.ChartParams
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope

interface ChartDeps {
  val networkListener: NetworkListener
  val pastPricesUseCase: PastPricesUseCase
  val stockPricesUseCase: StockPriceUseCase
  val dispatchersProvider: DispatchersProvider
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class ChartScope

@ChartScope
@Component(dependencies = [ChartDeps::class])
internal interface ChartComponent {
  @Component.Builder
  interface Builder {
    @BindsInstance
    fun params(chartParams: ChartParams): Builder

    fun dependencies(deps: ChartDeps): Builder
    fun build(): ChartComponent
  }

  fun viewModelFactory(): ChartViewModelFactory
}

internal class ChartComponentViewModel(
  deps: ChartDeps,
  params: ChartParams
) : ViewModel() {
  val component = DaggerChartComponent.builder()
    .dependencies(deps)
    .params(params)
    .build()
}

internal class ChartComponentViewModelFactory(
  private val deps: ChartDeps,
  private val params: ChartParams
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    require(modelClass == ChartComponentViewModel::class.java)
    return ChartComponentViewModel(deps, params) as T
  }
}