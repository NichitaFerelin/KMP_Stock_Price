package com.ferelin.core.data.repository

import com.ferelin.core.data.entity.marketNews.MarketNewsApi
import com.ferelin.core.data.entity.marketNews.MarketNewsDao
import com.ferelin.core.data.entity.marketNews.MarketRequestOptions
import com.ferelin.core.data.mapper.MarketNewsMapper
import com.ferelin.core.domain.entity.MarketNews
import com.ferelin.core.domain.repository.MarketNewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class MarketNewsRepositoryImpl(
    private val api: MarketNewsApi,
    private val dao: MarketNewsDao,
    private val token: String
) : MarketNewsRepository {
    override val marketNews: Flow<List<MarketNews>>
        get() = dao.getAll()
            .distinctUntilChanged()
            .map { it.map(MarketNewsMapper::map) }

    override suspend fun fetchMarketNews(): Result<Any> = runCatching {
        val requestOptions = MarketRequestOptions(token = token)
        val response = api.load(requestOptions)
        val dbMarketNews = response.map(MarketNewsMapper::map)

        dao.insertAll(dbMarketNews)
    }
}