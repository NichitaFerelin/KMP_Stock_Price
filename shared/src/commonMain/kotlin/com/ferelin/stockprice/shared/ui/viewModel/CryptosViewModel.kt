package com.ferelin.stockprice.shared.ui.viewModel

import com.ferelin.stockprice.shared.domain.entity.Crypto
import com.ferelin.stockprice.shared.domain.entity.LceState
import com.ferelin.stockprice.shared.domain.usecase.CryptoPriceUseCase
import com.ferelin.stockprice.shared.domain.usecase.CryptoUseCase
import com.ferelin.stockprice.shared.ui.DispatchersProvider
import com.ferelin.stockprice.shared.ui.mapper.CryptoMapper
import com.ferelin.stockprice.shared.ui.viewData.CryptoViewData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class CryptosStateUi(
  val cryptos: List<CryptoViewData> = emptyList(),
  val cryptosLce: LceState = LceState.None
)

class CryptosViewModel(
  private val cryptoPriceUseCase: CryptoPriceUseCase,
  private val viewModelScope: CoroutineScope,
  private val dispatchersProvider: DispatchersProvider,
  cryptoUseCase: CryptoUseCase,
) {
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

    /*networkListener.networkState
      .filter { it }
      .combine(
        flow = cryptoUseCase.cryptos,
        transform = { _, cryptos -> cryptos }
      )
      .onEach(this::onNetworkAvailable)
      .flowOn(dispatchersProvider.IO)
      .launchIn(viewModelScope)*/
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