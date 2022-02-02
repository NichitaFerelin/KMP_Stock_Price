package com.ferelin.core.domain.usecase

import android.util.Log
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.Crypto
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.repository.CryptoRepository
import dagger.Reusable
import kotlinx.coroutines.flow.*
import javax.inject.Inject

interface CryptoUseCase {
  val cryptos: Flow<List<Crypto>>
  val cryptosLce: Flow<LceState>
}

@Reusable
internal class CryptoUseCaseImpl @Inject constructor(
  cryptoRepository: CryptoRepository,
  dispatchersProvider: DispatchersProvider
) : CryptoUseCase {
  override val cryptos: Flow<List<Crypto>> = cryptoRepository.cryptos
    .onStart { cryptosLceState.value = LceState.Loading }
    .onEach { cryptosLceState.value = LceState.Content }
    .catch { e -> cryptosLceState.value = LceState.Error(e.message) }
    .flowOn(dispatchersProvider.IO)

  private val cryptosLceState = MutableStateFlow<LceState>(LceState.None)
  override val cryptosLce: Flow<LceState> = cryptosLceState.asStateFlow()
}