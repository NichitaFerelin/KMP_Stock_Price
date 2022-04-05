package com.ferelin.stockprice.data.entity.stockPrice

import com.ferelin.stockprice.db.StockPriceDBO
import com.ferelin.stockprice.db.StockPriceQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow

internal interface StockPriceDao {
  fun getAll(): Flow<List<StockPriceDBO>>
  suspend fun insert(stockPriceDBO: StockPriceDBO)
}

internal class StockPriceDaoImpl(
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