package com.ferelin.core.domain.repository

import com.ferelin.core.domain.entity.Crypto
import kotlinx.coroutines.flow.Flow

interface CryptoRepository {
  val cryptos: Flow<List<Crypto>>
}