package com.ferelin.core.domain.repository

import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.StockPrice
import kotlinx.coroutines.flow.Flow

interface StockPriceRepository {
    fun getBy(id: CompanyId): Flow<StockPrice?>
    suspend fun fetchPrice(companyId: CompanyId, companyTicker: String): Result<Any>
}