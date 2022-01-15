package com.ferelin.features.stocks.defaults

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import dagger.Component
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class DefaultStocksScope

@DefaultStocksScope
@Component(dependencies = [DefaultStocksDeps::class])
internal interface DefaultStocksComponent {
  @Component.Builder
  interface Builder {
    fun dependencies(deps: DefaultStocksDeps): Builder
    fun build(): DefaultStocksComponent
  }

  fun viewModelFactory(): DefaultStocksViewModelFactory
}

interface DefaultStocksDeps {
  val dispatchersProvider: DispatchersProvider
  val favouriteCompanyUseCase: FavouriteCompanyUseCase
  val companyUseCase: CompanyUseCase
}