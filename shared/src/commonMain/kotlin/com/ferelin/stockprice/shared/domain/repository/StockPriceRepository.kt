package com.ferelin.stockprice.shared.domain.repository

import com.ferelin.stockprice.shared.domain.entity.CompanyId
import com.ferelin.stockprice.shared.domain.entity.StockPrice
import kotlinx.coroutines.flow.Flow

interface StockPriceRepository {
    val stockPrice: Flow<List<StockPrice>>
    suspend fun fetchPrice(companyId: CompanyId, companyTicker: String)
    val fetchError: Flow<Exception?>
}