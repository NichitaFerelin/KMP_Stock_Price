package com.ferelin.core.data.entity.stockPrice

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import stockprice.StockPriceDBO
import stockprice.StockPriceQueries
import javax.inject.Inject

internal interface StockPriceDao {
  fun getAll(): Flow<List<StockPriceDBO>>
  suspend fun insert(stockPriceDBO: StockPriceDBO)
}

internal class StockPriceDaoImpl @Inject constructor(
  private val queries: StockPriceQueries
) : StockPriceDao {
  override fun getAll(): Flow<List<StockPriceDBO>> {
    return queries.getAll()
      .asFlow()
      .mapToList()
  }

  override suspend fun insert(stockPriceDBO: StockPriceDBO) {
    queries.insert(stockPriceDBO)
  }
}