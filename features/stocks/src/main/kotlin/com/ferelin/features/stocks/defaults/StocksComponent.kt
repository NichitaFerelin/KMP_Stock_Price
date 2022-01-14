package com.ferelin.features.stocks.defaults

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import dagger.Component
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class StocksScope

@StocksScope
@Component(dependencies = [StocksDeps::class])
internal interface StocksComponent {
  @Component.Builder
  interface Builder {
    fun dependencies(deps: StocksDeps): Builder
    fun build(): StocksComponent
  }

  fun viewModelFactory(): StocksViewModelFactory
}

interface StocksDeps {
  val dispatchersProvider: DispatchersProvider
  val favouriteCompanyUseCase: FavouriteCompanyUseCase
  val companyUseCase: CompanyUseCase
}