package com.ferelin.features.cryptos.cryptos

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.Crypto
import com.ferelin.core.domain.entity.CryptoPrice
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.entity.compare
import com.ferelin.core.domain.usecase.CryptoPriceUseCase
import com.ferelin.core.domain.usecase.CryptoUseCase
import com.ferelin.core.network.NetworkListener
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Immutable
internal data class CryptosUiState(
    val cryptos: List<CryptoViewData> = emptyList(),
    val cryptosLce: LceState = LceState.None,
    val cryptosFetchLce: LceState = LceState.None
)

internal class CryptosViewModel(
    private val cryptoUseCase: CryptoUseCase,
    private val cryptoPriceUseCase: CryptoPriceUseCase,
    private val dispatchersProvider: DispatchersProvider,
    networkListener: NetworkListener
) : ViewModel() {
    private val viewModelState = MutableStateFlow(CryptosUiState())
    val uiState: StateFlow<CryptosUiState> = viewModelState.asStateFlow()

    init {
        cryptoUseCase.cryptos
            .combine(
                flow = cryptoPriceUseCase.cryptoPrices,
                transform = { cryptos, prices -> cryptos.toCryptosViewData(prices) }
            )
            .flowOn(dispatchersProvider.IO)
            .onEach(this::onCryptos)
            .launchIn(viewModelScope)

        cryptoUseCase.cryptosLce
            .combine(
                flow = cryptoPriceUseCase.cryptoPricesLce,
                transform = { lce1, lce2 -> lce1.compare(lce2) }
            )
            .onEach(this::onCryptosLce)
            .launchIn(viewModelScope)

        cryptoPriceUseCase.cryptoPricesFetchLce
            .onEach(this::onCryptosFetchLce)
            .launchIn(viewModelScope)

        networkListener.networkState
            .filter { available -> available }
            .onEach { onNetworkAvailable() }
            .launchIn(viewModelScope)
    }

    fun fetchCryptos() {
        viewModelScope.launch(dispatchersProvider.IO) {
            val cryptos = cryptoUseCase.cryptos.firstOrNull() ?: return@launch
            cryptoPriceUseCase.fetchPriceFor(cryptos)
        }
    }

    private fun onNetworkAvailable() {
        fetchCryptos()
    }

    private fun onCryptos(cryptos: List<CryptoViewData>) {
        viewModelState.update { it.copy(cryptos = cryptos) }
    }

    private fun onCryptosLce(lceState: LceState) {
        viewModelState.update { it.copy(cryptosLce = lceState) }
    }

    private fun onCryptosFetchLce(lceState: LceState) {
        viewModelState.update { it.copy(cryptosFetchLce = lceState) }
    }
}

private fun List<Crypto>.toCryptosViewData(prices: List<CryptoPrice>): List<CryptoViewData> {
    return this.mapIndexed { index, crypto ->
        crypto.toCryptoViewData(prices.getOrNull(index))
    }
}