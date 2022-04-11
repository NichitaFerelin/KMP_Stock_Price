package com.ferelin.stockprice.shared.domain.repository

import com.ferelin.stockprice.shared.domain.entity.CompanyId
import com.ferelin.stockprice.shared.domain.entity.News
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    fun getAllBy(companyId: CompanyId): Flow<List<News>>
    suspend fun fetchNews(companyId: CompanyId, companyTicker: String)
    val fetchError: Flow<Exception?>
}