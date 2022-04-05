package com.ferelin.common.domain.repository

import com.ferelin.stockprice.domain.entity.Crypto
import kotlinx.coroutines.flow.Flow

interface CryptoRepository {
  val cryptos: Flow<List<Crypto>>
}