package com.ferelin.features.stocks.ui.main

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.domain.entity.Crypto
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.usecase.CryptoPriceUseCase
import com.ferelin.core.domain.usecase.CryptoUseCase
import com.ferelin.core.network.NetworkListener
import com.ferelin.core.ui.view.adapter.BaseRecyclerAdapter
import com.ferelin.core.ui.view.routing.Coordinator
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.LazyThreadSafetyMode.NONE

internal class MainViewModel @Inject constructor(
  private val cryptoPriceUseCase: CryptoPriceUseCase,
  private val coordinator: Coordinator,
  cryptoUseCase: CryptoUseCase,
  networkListener: NetworkListener
) : ViewModel() {
  init {
    networkListener.networkState
      .filter { it }
      .combine(
        flow = cryptoUseCase.cryptos,
        transform = { _, cryptos -> cryptos }
      )
      .onEach(this::onNetworkAvailable)
      .launchIn(viewModelScope)
  }

  val cryptos = cryptoUseCase.cryptos
    .combine(
      flow = cryptoPriceUseCase.cryptoPrices,
      transform = { cryptos, prices ->
        val pricesContainer = prices.associateBy { it.cryptoId }
        cryptos.map { CryptoMapper.map(it, pricesContainer[it.id]) }
      }
    )

  val cryptosLce = cryptoUseCase.cryptosLce
    .combine(
      flow = cryptoPriceUseCase.cryptoPricesLce,
      transform = { cryptoLce, priceLce ->
        if (cryptoLce is LceState.Loading || priceLce is LceState.Loading) {
          LceState.Loading
        } else priceLce
      }
    )
  val networkState = networkListener.networkState

  val cryptoAdapter: BaseRecyclerAdapter by lazy(NONE) {
    BaseRecyclerAdapter(
      createCryptoAdapter()
    ).apply { setHasStableIds(true) }
  }

  var lastSelectedPage = 0

  fun onSettingsClick() {
    // navigate
  }

  fun onSearchCardClick(sharedElement: View, name: String) {
    // navigate
    /*router.fromStocksPagerToSearch { fragmentTransaction ->
      fragmentTransaction.addSharedElement(sharedElement, name)
    }*/
  }

  private fun onNetworkAvailable(cryptos: List<Crypto>) {
    viewModelScope.launch {
      cryptoPriceUseCase.fetchPriceFor(cryptos)
    }
  }
}