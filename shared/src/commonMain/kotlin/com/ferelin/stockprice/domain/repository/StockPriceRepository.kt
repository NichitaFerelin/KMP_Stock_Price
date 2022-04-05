package com.ferelin.common.domain.repository

import com.ferelin.stockprice.domain.entity.CompanyId
import com.ferelin.stockprice.domain.entity.StockPrice
import kotlinx.coroutines.flow.Flow

interface StockPriceRepository {
  val stockPrice: Flow<List<StockPrice>>
  suspend fun fetchPrice(companyId: CompanyId, companyTicker: String)
  val fetchError: Flow<Exception?>
}