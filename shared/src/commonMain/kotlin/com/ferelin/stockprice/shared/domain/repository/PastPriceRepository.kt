package com.ferelin.stockprice.shared.domain.repository

import com.ferelin.stockprice.shared.domain.entity.CompanyId
import com.ferelin.stockprice.shared.domain.entity.PastPrice
import kotlinx.coroutines.flow.Flow

interface PastPriceRepository {
    fun getAllBy(companyId: CompanyId): Flow<List<PastPrice>>
    suspend fun fetchPastPrices(companyId: CompanyId, companyTicker: String)
    val fetchError: Flow<Exception?>
}