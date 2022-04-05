package com.ferelin.common.domain.repository

import com.ferelin.stockprice.domain.entity.Crypto
import com.ferelin.stockprice.domain.entity.CryptoPrice
import kotlinx.coroutines.flow.Flow

interface CryptoPriceRepository {
  val cryptoPrices: Flow<List<CryptoPrice>>
  suspend fun fetchPriceFor(cryptos: List<Crypto>)
  val fetchError: Flow<Exception?>
}