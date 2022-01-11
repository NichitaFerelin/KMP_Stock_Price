package com.ferelin.features.stocks.ui.favourites

import androidx.lifecycle.ViewModel
import com.ferelin.core.coroutine.DispatchersProvider
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
annotation class FavouriteStocksScope

@FavouriteStocksScope
@Component(dependencies = [FavouriteStocksDeps::class])
internal interface FavouriteStocksComponent {
  fun inject(favouriteStocksFragment: FavouriteStocksFragment)

  @Component.Builder
  interface Builder {
    fun dependencies(deps: FavouriteStocksDeps): Builder
    fun build(): FavouriteStocksComponent
  }
}

interface FavouriteStocksDeps {
  val coordinator: Coordinator
  val dispatchersProvider: DispatchersProvider
  val stockStyleProvider: StockStyleProvider
  val favouriteCompanyUseCase: FavouriteCompanyUseCase
  val companyUseCase: CompanyUseCase
}

interface FavouriteStocksDepsProvider {
  var deps: FavouriteStocksDeps

  companion object : FavouriteStocksDepsProvider by FavouriteStocksDepsStore
}

object FavouriteStocksDepsStore : FavouriteStocksDepsProvider {
  override var deps: FavouriteStocksDeps by Delegates.notNull()
}

internal class FavouriteStocksComponentViewModel : ViewModel() {
  val favouriteStocksComponent = DaggerFavouriteStocksComponent.builder()
    .dependencies(FavouriteStocksDepsStore.deps)
    .build()
}