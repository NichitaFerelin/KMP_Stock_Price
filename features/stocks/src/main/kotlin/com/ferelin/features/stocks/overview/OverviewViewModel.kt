package com.ferelin.features.stocks.overview

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.Crypto
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.usecase.CryptoPriceUseCase
import com.ferelin.core.domain.usecase.CryptoUseCase
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@Immutable
internal data class OverviewStateUi(
  val cryptos: List<CryptoViewData> = emptyList(),
  val cryptosLce: LceState = LceState.None,
  val showNetworkError: Boolean = false,
  val selectedScreenIndex: Int = DEFAULT_STOCKS_INDEX
)

internal class OverviewViewModel(
  private val cryptoPriceUseCase: CryptoPriceUseCase,
  private val dispatchersProvider: DispatchersProvider,
  cryptoUseCase: CryptoUseCase
) : ViewModel() {
  private val viewModelState = MutableStateFlow(OverviewStateUi())
  val uiState = viewModelState.asStateFlow()

  init {
    cryptoUseCase.cryptos
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.io())
      .map(CryptoMapper::map)
      .subscribe(this::onCryptos) { Timber.e(it) }

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

internal class OverviewViewModelFactory @Inject constructor(
  private val cryptoPriceUseCase: CryptoPriceUseCase,
  private val cryptoUseCase: CryptoUseCase,
  private val dispatchersProvider: DispatchersProvider
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    require(modelClass == OverviewViewModel::class.java)
    return OverviewViewModel(
      cryptoPriceUseCase,
      dispatchersProvider,
      cryptoUseCase,
    ) as T
  }
}