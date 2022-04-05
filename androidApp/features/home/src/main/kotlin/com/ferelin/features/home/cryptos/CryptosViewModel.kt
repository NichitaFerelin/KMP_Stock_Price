package com.ferelin.features.home.cryptos

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.network.NetworkListener
import com.ferelin.core.ui.mapper.CryptoMapper
import com.ferelin.core.ui.viewData.CryptoViewData
import com.ferelin.stockprice.domain.entity.Crypto
import com.ferelin.stockprice.domain.entity.LceState
import com.ferelin.common.domain.usecase.CryptoPriceUseCase
import com.ferelin.common.domain.usecase.CryptoUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Immutable
internal data class CryptosStateUi(
  val cryptos: List<CryptoViewData> = emptyList(),
  val cryptosLce: LceState = LceState.None
)

internal class CryptosViewModel(
  private val cryptoPriceUseCase: CryptoPriceUseCase,
  private val dispatchersProvider: DispatchersProvider,
  cryptoUseCase: CryptoUseCase,
  networkListener: NetworkListener
) : ViewModel() {
  private val viewModelState = MutableStateFlow(CryptosStateUi())
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
      .filter { it }
      .combine(
        flow = cryptoUseCase.cryptos,
        transform = { _, cryptos -> cryptos }
      )
      .onEach(this::onNetworkAvailable)
      .flowOn(dispatchersProvider.IO)
      .launchIn(viewModelScope)
  }

  private fun onCryptos(cryptos: List<CryptoViewData>) {
    viewModelState.update { it.copy(cryptos = cryptos) }
  }

  private fun onCryptosLce(lceState: LceState) {
    viewModelState.update { it.copy(cryptosLce = lceState) }
  }

  private fun onNetworkAvailable(cryptos: List<Crypto>) {
    viewModelScope.launch(dispatchersProvider.IO) {
      cryptoPriceUseCase.fetchPriceFor(cryptos)
    }
  }
}