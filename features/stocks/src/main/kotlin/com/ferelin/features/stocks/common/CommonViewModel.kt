package com.ferelin.features.stocks.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.Crypto
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.usecase.CryptoPriceUseCase
import com.ferelin.core.domain.usecase.CryptoUseCase
import com.ferelin.core.network.NetworkListener
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CommonStateUi(
  val cryptos: List<CryptoViewData> = emptyList(),
  val cryptosLce: LceState = LceState.None,
  val showNetworkError: Boolean = false,
  val selectedScreenIndex: Int = STOCKS_INDEX
)

class CommonViewModel @Inject constructor(
  private val cryptoPriceUseCase: CryptoPriceUseCase,
  private val dispatchersProvider: DispatchersProvider,
  cryptoUseCase: CryptoUseCase,
  networkListener: NetworkListener
) : ViewModel() {
  private val viewModelState = MutableStateFlow(CommonStateUi())
  val uiState = viewModelState.asStateFlow()

  init {
    cryptoUseCase.cryptos
      .combine(
        flow = cryptoPriceUseCase.cryptoPrices,
        transform = { cryptos, prices ->
          val pricesContainer = prices.associateBy { it.cryptoId }
          cryptos.map { CryptoMapper.map(it, pricesContainer[it.id]) }
        }
      )
      .onEach(this::onCryptos)
      .flowOn(dispatchersProvider.IO)
      .launchIn(viewModelScope)

    cryptoUseCase.cryptosLce
      .combine(
        flow = cryptoPriceUseCase.cryptoPricesLce,
        transform = { cryptoLce, priceLce ->
          if (cryptoLce is LceState.Loading || priceLce is LceState.Loading) {
            LceState.Loading
          } else priceLce
        }
      )
      .onEach(this::onCryptosLce)
      .launchIn(viewModelScope)

    networkListener.networkState
      .onEach(this::onNetwork)
      .filter { it }
      .combine(
        flow = cryptoUseCase.cryptos,
        transform = { _, cryptos -> cryptos }
      )
      .onEach(this::onNetworkAvailable)
      .flowOn(dispatchersProvider.IO)
      .launchIn(viewModelScope)
  }

  fun onScreenSelected(index: Int) {
    viewModelState.update { it.copy(selectedScreenIndex = index) }
  }

  private fun onCryptos(cryptos: List<CryptoViewData>) {
    viewModelState.update { it.copy(cryptos = cryptos) }
  }

  private fun onCryptosLce(lceState: LceState) {
    viewModelState.update { it.copy(cryptosLce = lceState) }
  }

  private fun onNetwork(isAvailable: Boolean) {
    viewModelState.update { it.copy(showNetworkError = !isAvailable) }
  }

  private fun onNetworkAvailable(cryptos: List<Crypto>) {
    viewModelScope.launch(dispatchersProvider.IO) {
      cryptoPriceUseCase.fetchPriceFor(cryptos)
    }
  }
}