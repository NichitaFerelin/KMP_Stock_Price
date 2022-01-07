package com.ferelin.features.stocks.domain.repository

import com.ferelin.features.stocks.domain.entity.Crypto
import com.ferelin.features.stocks.domain.entity.CryptoId
import com.ferelin.features.stocks.domain.entity.CryptoPrice
import kotlinx.coroutines.flow.Flow

interface CryptoRepository {
  val cryptos: Flow<List<Crypto>>
}