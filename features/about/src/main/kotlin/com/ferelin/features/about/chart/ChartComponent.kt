package com.ferelin.features.about.chart

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.PastPricesUseCase
import com.ferelin.core.domain.usecase.StockPriceUseCase
import com.ferelin.core.network.NetworkListener
import com.ferelin.core.ui.params.ChartParams
import dagger.BindsInstance
import dagger.Component
import javax.inject.Scope
import kotlin.properties.Delegates

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

interface ChartDeps {
  val networkListener: NetworkListener
  val pastPricesUseCase: PastPricesUseCase
  val stockPricesUseCase: StockPriceUseCase
  val dispatchersProvider: DispatchersProvider
}