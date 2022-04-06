package com.ferelin.common.domain.repository

import com.ferelin.stockprice.shared.commonMain.domain.entity.CompanyId
import com.ferelin.stockprice.shared.commonMain.domain.entity.PastPrice
import kotlinx.coroutines.flow.Flow

interface PastPriceRepository {
  fun getAllBy(companyId: CompanyId): Flow<List<PastPrice>>
  suspend fun fetchPastPrices(companyId: CompanyId, companyTicker: String)
  val fetchError: Flow<Exception?>
}