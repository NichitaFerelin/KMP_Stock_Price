package com.ferelin.stockprice.shared.commonMain.data.entity.cryptoPrice

import com.ferelin.stockprice.db.CryptoPriceDBO
import com.ferelin.stockprice.db.CryptoPriceQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow

internal interface CryptoPriceDao {
  fun getAll(): Flow<List<CryptoPriceDBO>>
  suspend fun insertAll(cryptoPricesDBO: List<CryptoPriceDBO>)
}

internal class CryptoPriceDaoImpl(
  private val queries: CryptoPriceQueries
) : CryptoPriceDao {
  override fun getAll(): Flow<List<CryptoPriceDBO>> {
    return queries.getAll()
      .asFlow()
      .mapToList()
  }

  override suspend fun insertAll(cryptoPricesDBO: List<CryptoPriceDBO>) {
    queries.transaction {
      cryptoPricesDBO.forEach {
        queries.insert(it)
      }
    }
  }
}