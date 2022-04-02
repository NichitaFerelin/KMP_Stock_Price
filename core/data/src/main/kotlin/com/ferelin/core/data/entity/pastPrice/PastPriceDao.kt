package com.ferelin.core.data.entity.pastPrice

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import stockprice.PastPriceDBO
import stockprice.PastPriceQueries

internal interface PastPriceDao {
  fun getAllBy(companyId: Int): Flow<List<PastPriceDBO>>
  suspend fun insertAll(pastPricesDBO: List<PastPriceDBO>)
  suspend fun eraseAllBy(companyId: Int)
}

internal class PastPriceDaoImpl(
  private val queries: PastPriceQueries
) : PastPriceDao {
  override fun getAllBy(companyId: Int): Flow<List<PastPriceDBO>> {
    return queries.getAllBy(companyId)
      .asFlow()
      .mapToList()
  }

  override suspend fun insertAll(pastPricesDBO: List<PastPriceDBO>) {
    queries.transaction {
      pastPricesDBO.forEach {
        queries.insert(
          id = null,
          companyId = it.companyId,
          closePrice = it.closePrice,
          dateMillis = it.dateMillis
        )
      }
    }
  }

  override suspend fun eraseAllBy(companyId: Int) {
    queries.eraseAllBy(companyId)
  }
}