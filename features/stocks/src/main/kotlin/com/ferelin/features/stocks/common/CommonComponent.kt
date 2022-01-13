package com.ferelin.features.stocks.common

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.CryptoPriceUseCase
import com.ferelin.core.domain.usecase.CryptoUseCase
import com.ferelin.core.network.NetworkListener
import dagger.Component
import javax.inject.Scope

@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class CommonScope

@CommonScope
@Component(dependencies = [CommonDeps::class])
interface CommonComponent {
  @Component.Builder
  interface Builder {
    fun dependencies(deps: CommonDeps): Builder
    fun build(): CommonComponent
  }

  fun viewModelFactory() : CommonViewModelFactory
}

interface CommonDeps {
  val cryptoPriceUseCase: CryptoPriceUseCase
  val cryptoUseCase: CryptoUseCase
  val networkListener: NetworkListener
  val dispatchersProvider: DispatchersProvider
}