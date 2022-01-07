package com.ferelin.features.about.domain.repositories

import com.ferelin.core.domain.entities.entity.CompanyId
import com.ferelin.features.about.domain.entities.PastPrice
import kotlinx.coroutines.flow.Flow

interface PastPricesRepository {
  fun getAllBy(companyId: CompanyId): Flow<List<PastPrice>>
  suspend fun fetchPastPrices(companyId: CompanyId, companyTicker: String)
  val fetchError: Flow<Exception?>
}