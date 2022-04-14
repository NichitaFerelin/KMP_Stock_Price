package com.ferelin.core.domain.usecase

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.Crypto
import com.ferelin.core.domain.entity.CryptoPrice
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.repository.CryptoPriceRepository
import kotlinx.coroutines.flow.*

interface CryptoPriceUseCase {
    val cryptoPrices: Flow<List<CryptoPrice>>
    val cryptoPricesLce: Flow<LceState>
    suspend fun fetchPriceFor(cryptos: List<Crypto>): Result<Any>
}

internal class CryptoPriceUseCaseImpl(
    private val cryptoPriceRepository: CryptoPriceRepository,
    private val dispatchersProvider: DispatchersProvider
) : CryptoPriceUseCase {
    override val cryptoPrices: Flow<List<CryptoPrice>>
        get() = cryptoPriceRepository.cryptoPrices
            .onStart { cryptoPricesLceState.value = LceState.Loading }
            .onEach { cryptoPricesLceState.value = LceState.Content }
            .catch { e -> cryptoPricesLceState.value = LceState.Error(e.message) }
            .flowOn(dispatchersProvider.IO)

    override suspend fun fetchPriceFor(cryptos: List<Crypto>): Result<Any> {
        return cryptoPriceRepository.fetchPriceFor(cryptos)
    }

    private val cryptoPricesLceState = MutableStateFlow<LceState>(LceState.None)
    override val cryptoPricesLce: Flow<LceState> = cryptoPricesLceState.asStateFlow()
}