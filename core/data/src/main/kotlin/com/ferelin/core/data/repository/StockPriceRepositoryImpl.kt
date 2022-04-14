package com.ferelin.core.data.repository

import com.ferelin.core.data.entity.stockPrice.StockPriceApi
import com.ferelin.core.data.entity.stockPrice.StockPriceDao
import com.ferelin.core.data.entity.stockPrice.StockPriceOptions
import com.ferelin.core.data.mapper.StockPriceMapper
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.StockPrice
import com.ferelin.core.domain.repository.StockPriceRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

internal class StockPriceRepositoryImpl(
    private val dao: StockPriceDao,
    private val api: StockPriceApi,
    private val token: String
) : StockPriceRepository {
    override val stockPrice: Flow<List<StockPrice>>
        get() = dao.getAll()
            .distinctUntilChanged()
            .map { it.map(StockPriceMapper::map) }

    override suspend fun fetchPrice(
        companyId: CompanyId,
        companyTicker: String
    ): Result<Any> = runCatching {
        val requestOptions = StockPriceOptions(token, companyTicker)
        val response = api.load(requestOptions)
        val dbStockPrices = StockPriceMapper.map(response, companyId)

        dao.insert(dbStockPrices)
    }
}