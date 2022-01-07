package com.ferelin.features.stocks.domain

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entities.entity.LceState
import com.ferelin.features.stocks.domain.entity.Crypto
import com.ferelin.features.stocks.domain.repository.CryptoRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

interface CryptoUseCase {
  val cryptos: Flow<List<Crypto>>
  val cryptosLce: Flow<LceState>
}

internal class CryptoUseCaseImpl @Inject constructor(
  private val cryptoRepository: CryptoRepository,
  private val dispatchersProvider: DispatchersProvider
) : CryptoUseCase {
  override val cryptos: Flow<List<Crypto>> = cryptoRepository.cryptos
    .onStart { cryptosLceState.value = LceState.Loading }
    .onEach { cryptosLceState.value = LceState.Content }
    .catch { e -> cryptosLceState.value = LceState.Error(e.message) }
    .flowOn(dispatchersProvider.IO)

  private val cryptosLceState = MutableStateFlow<LceState>(LceState.None)
  override val cryptosLce: Flow<LceState> = cryptosLceState.asStateFlow()
}