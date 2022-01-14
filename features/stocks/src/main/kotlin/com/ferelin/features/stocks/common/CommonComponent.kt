package com.ferelin.features.stocks.common

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.CryptoPriceUseCase
import com.ferelin.core.domain.usecase.CryptoUseCase
import com.ferelin.core.network.NetworkListener
import com.ferelin.features.stocks.defaults.StocksDeps
import com.ferelin.features.stocks.favourites.FavouriteStocksDeps
import dagger.Component
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class CommonScope

@CommonScope
@Component(dependencies = [CommonDeps::class])
internal interface CommonComponent {
  @Component.Builder
  interface Builder {
    fun dependencies(deps: CommonDeps): Builder
    fun build(): CommonComponent
  }

  fun viewModelFactory() : CommonViewModelFactory
}

interface CommonDeps : StocksDeps, FavouriteStocksDeps {
  val cryptoPriceUseCase: CryptoPriceUseCase
  val cryptoUseCase: CryptoUseCase
  val networkListener: NetworkListener
}