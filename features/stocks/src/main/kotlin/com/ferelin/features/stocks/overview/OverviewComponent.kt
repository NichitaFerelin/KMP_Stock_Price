package com.ferelin.features.stocks.overview

import com.ferelin.core.domain.usecase.CryptoPriceUseCase
import com.ferelin.core.domain.usecase.CryptoUseCase
import com.ferelin.core.network.NetworkListener
import com.ferelin.features.stocks.defaults.DefaultStocksDeps
import com.ferelin.features.stocks.favourites.FavouriteStocksDeps
import dagger.Component
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class OverviewScope

@OverviewScope
@Component(dependencies = [OverviewDeps::class])
internal interface OverviewComponent {
  @Component.Builder
  interface Builder {
    fun dependencies(deps: OverviewDeps): Builder
    fun build(): OverviewComponent
  }

  fun viewModelFactory(): OverviewViewModelFactory
}

interface OverviewDeps : DefaultStocksDeps, FavouriteStocksDeps {
  val cryptoPriceUseCase: CryptoPriceUseCase
  val cryptoUseCase: CryptoUseCase
  val networkListener: NetworkListener
}