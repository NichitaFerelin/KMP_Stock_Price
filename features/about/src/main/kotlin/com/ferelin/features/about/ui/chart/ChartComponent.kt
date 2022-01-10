package com.ferelin.features.about.ui.chart

import androidx.lifecycle.ViewModel
import com.ferelin.core.domain.usecase.PastPricesUseCase
import com.ferelin.core.domain.usecase.StockPriceUseCase
import com.ferelin.core.network.NetworkListener
import dagger.Component
import javax.inject.Scope
import kotlin.properties.Delegates

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class ChartScope

@ChartScope
@Component(dependencies = [ChartDeps::class])
internal interface ChartComponent {
  fun inject(chartFragment: ChartFragment)

  @Component.Builder
  interface Builder {
    fun dependencies(deps: ChartDeps): Builder
    fun build(): ChartComponent
  }
}

interface ChartDeps {
  val networkListener: NetworkListener
  val pastPricesUseCase: PastPricesUseCase
  val stockPricesUseCase: StockPriceUseCase
}

interface ChartDepsProvider {
  var deps: ChartDeps

  companion object : ChartDepsProvider by ChartDepsStore
}

object ChartDepsStore : ChartDepsProvider {
  override var deps: ChartDeps by Delegates.notNull()
}

internal class ChartComponentViewModel : ViewModel() {
  val chartComponent = DaggerChartComponent.builder()
    .dependencies(ChartDepsStore.deps)
    .build()
}