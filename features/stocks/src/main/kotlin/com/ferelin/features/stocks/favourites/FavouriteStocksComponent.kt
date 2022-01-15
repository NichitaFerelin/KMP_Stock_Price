package com.ferelin.features.stocks.favourites

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import dagger.Component
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class FavouriteStocksScope

@FavouriteStocksScope
@Component(dependencies = [FavouriteStocksDeps::class])
internal interface FavouriteStocksComponent {
  @Component.Builder
  interface Builder {
    fun dependencies(deps: FavouriteStocksDeps): Builder
    fun build(): FavouriteStocksComponent
  }

  fun viewModelFactory(): FavouriteStocksViewModelFactory
}

interface FavouriteStocksDeps {
  val dispatchersProvider: DispatchersProvider
  val favouriteCompanyUseCase: FavouriteCompanyUseCase
  val companyUseCase: CompanyUseCase
}