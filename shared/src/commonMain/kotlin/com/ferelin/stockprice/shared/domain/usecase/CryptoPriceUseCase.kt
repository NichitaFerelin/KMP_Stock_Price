package com.ferelin.stockprice.shared.domain.usecase

import com.ferelin.stockprice.shared.domain.entity.Crypto
import com.ferelin.stockprice.shared.domain.entity.CryptoPrice
import com.ferelin.stockprice.shared.domain.entity.LceState
import com.ferelin.stockprice.shared.domain.repository.CryptoPriceRepository
import kotlinx.coroutines.flow.*

interface CryptoPriceUseCase {
    val cryptoPrices: Flow<List<CryptoPrice>>
    suspend fun fetchPriceFor(cryptos: List<Crypto>)
    val cryptoPricesLce: Flow<LceState>
}

internal class CryptoPriceUseCaseImpl(
    private val cryptoPriceRepository: CryptoPriceRepository
) : CryptoPriceUseCase {
    override val cryptoPrices: Flow<List<CryptoPrice>>
        get() = cryptoPriceRepository.cryptoPrices
            .zip(
                other = cryptoPriceRepository.fetchError,
                transform = { cryptoPrices, exception ->
                    if (exception != null) {
                        cryptoPricesLceState.value = LceState.Error(exception.message)
                    }
                    cryptoPrices
                }
            )
            .onStart { cryptoPricesLceState.value = LceState.Loading }
            .onEach { cryptoPricesLceState.value = LceState.Content }
            .catch { e -> cryptoPricesLceState.value = LceState.Error(e.message) }

    override suspend fun fetchPriceFor(cryptos: List<Crypto>) {
        cryptoPricesLceState.value = LceState.Loading
        cryptoPriceRepository.fetchPriceFor(cryptos)
        cryptoPricesLceState.value = LceState.Content
    }

    private val cryptoPricesLceState = MutableStateFlow<LceState>(LceState.None)
    override val cryptoPricesLce: Flow<LceState> = cryptoPricesLceState.asStateFlow()
}