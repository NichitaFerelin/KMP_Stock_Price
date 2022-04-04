package com.ferelin.core.data.entity.company

import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow
import stockprice.CompanyDBO
import stockprice.CompanyQueries

internal interface CompanyDao {
  fun getAll(): Flow<List<CompanyDBO>>
  suspend fun insertAll(companies: List<CompanyDBO>)
}

internal class CompanyDaoImpl(
  private val queries: CompanyQueries
) : CompanyDao {
  override fun getAll(): Flow<List<CompanyDBO>> {
    return queries.getAll()
      .asFlow()
      .mapToList()
  }

  override suspend fun insertAll(companies: List<CompanyDBO>) {
    queries.transaction {
      companies.forEach {
        queries.insert(it)
      }
    }
  }
}