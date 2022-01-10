package com.ferelin.features.stocks.ui.defaults

import androidx.lifecycle.ViewModel
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.core.domain.usecase.StockPriceUseCase
import com.ferelin.core.ui.view.routing.Coordinator
import com.ferelin.core.ui.viewData.utils.StockStyleProvider
import dagger.Component
import javax.inject.Scope
import kotlin.properties.Delegates

@Scope
@Retention(AnnotationRetention.RUNTIME)
annotation class StocksScope

@StocksScope
@Component(dependencies = [StocksDeps::class])
internal interface StocksComponent {
  fun inject(stocksFragment: StocksFragment)

  @Component.Builder
  interface Builder {
    fun dependencies(deps: StocksDeps): Builder
    fun build(): StocksComponent
  }
}

interface StocksDeps {
  val coordinator: Coordinator
  val stockStyleProvider: StockStyleProvider
  val favouriteCompanyUseCase: FavouriteCompanyUseCase
  val stockPriceUseCase: StockPriceUseCase
  val companyUseCase: CompanyUseCase
}

interface StocksDepsProvider {
  var deps: StocksDeps

  companion object : StocksDepsProvider by StocksDepsStore
}

object StocksDepsStore : StocksDepsProvider {
  override var deps: StocksDeps by Delegates.notNull()
}

internal class StocksComponentViewModel : ViewModel() {
  val stocksComponent = DaggerStocksComponent.builder()
    .dependencies(StocksDepsStore.deps)
    .build()
}