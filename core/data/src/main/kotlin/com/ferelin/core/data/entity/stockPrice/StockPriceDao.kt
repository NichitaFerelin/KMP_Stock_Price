package com.ferelin.core.data.entity.stockPrice

import com.squareup.sqldelight.runtime.coroutines.asFlow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import stockprice.StockPriceDBO
import stockprice.StockPriceQueries

internal interface StockPriceDao {
    fun getBy(id: Int): Flow<StockPriceDBO?>
    suspend fun insert(stockPriceDBO: StockPriceDBO)
}

internal class StockPriceDaoImpl(
    private val queries: StockPriceQueries
) : StockPriceDao {
    override fun getBy(id: Int): Flow<StockPriceDBO?> {
        return queries.getBy(id)
            .asFlow()
            .map { it.executeAsOneOrNull() }
    }

    override suspend fun insert(stockPriceDBO: StockPriceDBO) {
        queries.insert(stockPriceDBO)
    }
}