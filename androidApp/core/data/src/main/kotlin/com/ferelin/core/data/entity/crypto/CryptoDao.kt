package com.ferelin.core.data.entity.crypto

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import stockprice.CryptoDBO
import stockprice.CryptoQueries

internal interface CryptoDao {
  fun getAll(): Flow<List<CryptoDBO>>
  suspend fun insertAll(cryptosDBO: List<CryptoDBO>)
}

internal class CryptoDaoImpl(
  private val queries: CryptoQueries
) : CryptoDao {
  override fun getAll(): Flow<List<CryptoDBO>> {
    return queries.getAll()
      .asFlow()
      .mapToList()
  }

  override suspend fun insertAll(cryptosDBO: List<CryptoDBO>) {
    queries.transaction {
      cryptosDBO.forEach {
        queries.insert(it)
      }
    }
  }
}