package com.ferelin.core.data.entity.searchRequest

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import stockprice.SearchRequestDBO
import stockprice.SearchRequestQueries

internal interface SearchRequestDao {
  fun getAll(): Flow<List<SearchRequestDBO>>
  suspend fun insert(request: String)
  suspend fun insertAll(requests: List<String>)
  suspend fun eraseBy(id: Int)
  suspend fun eraseAll()
}

internal class SearchRequestDaoImpl(
  private val queries: SearchRequestQueries
) : SearchRequestDao {
  override fun getAll(): Flow<List<SearchRequestDBO>> {
    return queries.getAll()
      .asFlow()
      .mapToList()
  }

  override suspend fun insert(request: String) {
    queries.insert(id = null, request = request)
  }

  override suspend fun insertAll(requests: List<String>) {
    queries.transaction {
      requests.forEach {
        queries.insert(id = null, request = it)
      }
    }
  }

  override suspend fun eraseBy(id: Int) {
    queries.eraseBy(id)
  }

  override suspend fun eraseAll() {
    queries.eraseAll()
  }
}