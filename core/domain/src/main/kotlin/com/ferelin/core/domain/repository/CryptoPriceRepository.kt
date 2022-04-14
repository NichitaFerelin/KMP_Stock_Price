package com.ferelin.core.domain.repository

import com.ferelin.core.domain.entity.Crypto
import com.ferelin.core.domain.entity.CryptoPrice
import kotlinx.coroutines.flow.Flow

interface CryptoPriceRepository {
    val cryptoPrices: Flow<List<CryptoPrice>>
    suspend fun fetchPriceFor(cryptos: List<Crypto>): Result<Any>
}