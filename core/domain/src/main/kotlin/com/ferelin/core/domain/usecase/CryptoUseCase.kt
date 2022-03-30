package com.ferelin.core.domain.usecase

import com.ferelin.core.domain.entity.Crypto
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.repository.CryptoRepository
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

interface CryptoUseCase {
  val cryptos: Observable<List<Crypto>>
  val cryptosLce: Flow<LceState>
}

internal class CryptoUseCaseImpl @Inject constructor(
  cryptoRepository: CryptoRepository,
) : CryptoUseCase {
  override val cryptos: Observable<List<Crypto>> = cryptoRepository.cryptos
    .doOnSubscribe { cryptosLceState.value = LceState.Loading }
    .doOnEach { cryptosLceState.value = LceState.Content }
    .doOnError { e -> cryptosLceState.value = LceState.Error(e.message) }

  private val cryptosLceState = MutableStateFlow<LceState>(LceState.None)
  override val cryptosLce: Flow<LceState> = cryptosLceState.asStateFlow()
}