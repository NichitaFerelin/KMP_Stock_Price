package com.ferelin.features.home.cryptos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.CryptoPriceUseCase
import com.ferelin.core.domain.usecase.CryptoUseCase
import com.ferelin.core.network.NetworkListener
import dagger.Component
import javax.inject.Scope

interface CryptosDeps {
  val cryptoPriceUseCase: CryptoPriceUseCase
  val cryptoUseCase: CryptoUseCase
  val networkListener: NetworkListener
  val dispatchersProvider: DispatchersProvider
}

@Scope
@Retention(AnnotationRetention.RUNTIME)
internal annotation class CryptosScope

@CryptosScope
@Component(dependencies = [CryptosDeps::class])
internal interface CryptosComponent {
  @Component.Builder
  interface Builder {
    fun dependencies(deps: CryptosDeps): Builder
    fun build(): CryptosComponent
  }

  fun viewModelFactory(): CryptosViewModelFactory
}

internal class CryptosComponentViewModel(deps: CryptosDeps) : ViewModel() {
  val component = DaggerCryptosComponent.builder()
    .dependencies(deps)
    .build()
}

internal class CryptosComponentViewModelFactory(
  private val deps: CryptosDeps
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    require(modelClass == CryptosComponentViewModel::class.java)
    return CryptosComponentViewModel(deps) as T
  }
}