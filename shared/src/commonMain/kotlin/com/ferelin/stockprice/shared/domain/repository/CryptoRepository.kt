package com.ferelin.stockprice.shared.domain.repository

import com.ferelin.stockprice.shared.domain.entity.Crypto
import kotlinx.coroutines.flow.Flow

interface CryptoRepository {
    val cryptos: Flow<List<Crypto>>
}