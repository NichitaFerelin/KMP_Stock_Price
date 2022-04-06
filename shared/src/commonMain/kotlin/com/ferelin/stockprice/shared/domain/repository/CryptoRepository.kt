package com.ferelin.stockprice.shared.domain.repository

import com.ferelin.stockprice.androidApp.domain.entity.Crypto
import kotlinx.coroutines.flow.Flow

interface CryptoRepository {
  val cryptos: Flow<List<Crypto>>
}