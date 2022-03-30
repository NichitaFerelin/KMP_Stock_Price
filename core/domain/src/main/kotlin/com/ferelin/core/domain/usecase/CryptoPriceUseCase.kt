package com.ferelin.core.domain.usecase

import com.ferelin.core.domain.entity.Crypto
import com.ferelin.core.domain.entity.CryptoPrice
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.repository.CryptoPriceRepository
import dagger.Reusable
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

interface CryptoPriceUseCase {
  val cryptoPrices: Observable<List<CryptoPrice>>
  val cryptoPricesLce: Flow<LceState>
  fun fetchPriceFor(cryptos: List<Crypto>)
}

@Reusable
internal class CryptoPriceUseCaseImpl @Inject constructor(
  private val cryptoPriceRepository: CryptoPriceRepository
) : CryptoPriceUseCase {
  override val cryptoPrices: Observable<List<CryptoPrice>>
    get() = cryptoPriceRepository.cryptoPrices
      .doOnSubscribe { cryptoPricesLceState.value = LceState.Loading }
      .doOnEach { cryptoPricesLceState.value = LceState.Content }
      .doOnError { e -> cryptoPricesLceState.value = LceState.Error(e.message) }

  override fun fetchPriceFor(cryptos: List<Crypto>) {
    cryptoPriceRepository
      .fetchPriceFor(cryptos)
      .doOnSubscribe { cryptoPricesLceState.value = LceState.Loading }
      .doOnComplete { cryptoPricesLceState.value = LceState.Content }
      .doOnError { cryptoPricesLceState.value = LceState.Error(it.message) }
      .blockingAwait()
  }

  private val cryptoPricesLceState = MutableStateFlow<LceState>(LceState.None)
  override val cryptoPricesLce: Flow<LceState> = cryptoPricesLceState.asStateFlow()
}