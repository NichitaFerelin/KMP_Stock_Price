package com.ferelin.common.domain.usecase

import com.ferelin.stockprice.shared.commonMain.domain.entity.Crypto
import com.ferelin.stockprice.shared.commonMain.domain.entity.LceState
import com.ferelin.common.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.*

interface CryptoUseCase {
  val cryptos: Flow<List<Crypto>>
  val cryptosLce: Flow<LceState>
}

internal class CryptoUseCaseImpl(
  cryptoRepository: CryptoRepository
) : CryptoUseCase {
  override val cryptos: Flow<List<Crypto>> = cryptoRepository.cryptos
    .onStart { cryptosLceState.value = LceState.Loading }
    .onEach { cryptosLceState.value = LceState.Content }
    .catch { e -> cryptosLceState.value = LceState.Error(e.message) }

  private val cryptosLceState = MutableStateFlow<LceState>(LceState.None)
  override val cryptosLce: Flow<LceState> = cryptosLceState.asStateFlow()
}