package com.ferelin.core.data.entity.marketNews

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import stockprice.MarketNewsDBO
import stockprice.MarketNewsQueries

internal interface MarketNewsDao {
    fun getAll(): Flow<List<MarketNewsDBO>>
    suspend fun insertAll(marketNewsDBO: List<MarketNewsDBO>)
}

internal class MarketNewsDaoImpl(
    private val queries: MarketNewsQueries
) : MarketNewsDao {
    override fun getAll(): Flow<List<MarketNewsDBO>> {
        return queries.getAll()
            .asFlow()
            .mapToList()
    }

    override suspend fun insertAll(marketNewsDBO: List<MarketNewsDBO>) {
        queries.transaction {
            marketNewsDBO.forEach {
                queries.insert(it)
            }
        }
    }
}