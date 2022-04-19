package com.ferelin.core.domain.repository

import com.ferelin.core.domain.entity.MarketNews
import kotlinx.coroutines.flow.Flow

interface MarketNewsRepository {
    val marketNews: Flow<List<MarketNews>>
    suspend fun fetchMarketNews(): Result<Any>
}