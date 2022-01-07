package com.ferelin.core.domain.entities.repository

import com.ferelin.core.domain.entities.entity.CompanyId
import com.ferelin.core.domain.entities.entity.StockPrice
import kotlinx.coroutines.flow.Flow

interface StockPriceRepository {
  val stockPrice: Flow<List<StockPrice>>
  suspend fun fetchPrice(companyId: CompanyId, companyTicker: String)
  val fetchError: Flow<Exception?>
}