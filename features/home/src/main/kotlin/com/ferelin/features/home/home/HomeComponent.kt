package com.ferelin.features.home.home

import com.ferelin.features.home.cryptos.CryptosDeps
import com.ferelin.features.home.favourite.FavouriteStocksDeps
import com.ferelin.features.home.stocks.StocksDeps
import dagger.Component
import javax.inject.Scope

interface HomeDeps : StocksDeps, FavouriteStocksDeps, CryptosDeps

@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class HomeScope

@HomeScope
@Component(dependencies = [HomeDeps::class])
internal interface HomeComponent {
  @Component.Builder
  interface Builder {
    fun dependencies(deps: HomeDeps): Builder
    fun build(): HomeComponent
  }
}