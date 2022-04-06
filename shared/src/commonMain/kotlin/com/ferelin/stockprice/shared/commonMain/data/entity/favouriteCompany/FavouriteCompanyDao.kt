package com.ferelin.stockprice.shared.commonMain.data.entity.favouriteCompany

import com.ferelin.stockprice.db.FavouriteCompanyQueries
import com.squareup.sqldelight.runtime.coroutines.asFlow
import com.squareup.sqldelight.runtime.coroutines.mapToList
import kotlinx.coroutines.flow.Flow

internal interface FavouriteCompanyDao {
  fun getAll(): Flow<List<Int>>
  suspend fun insert(favouriteCompanyId: Int)
  suspend fun insertAll(favouriteCompanyIds: List<Int>)
  suspend fun eraseBy(id: Int)
  suspend fun eraseAll()
}

internal class FavouriteCompanyDaoImpl(
  private val queries: FavouriteCompanyQueries
) : FavouriteCompanyDao {
  override fun getAll(): Flow<List<Int>> {
    return queries.getAll()
      .asFlow()
      .mapToList()
  }

  override suspend fun insert(favouriteCompanyId: Int) {
    queries.insert(favouriteCompanyId)
  }

  override suspend fun insertAll(favouriteCompanyIds: List<Int>) {
    queries.transaction {
      favouriteCompanyIds.forEach {
        queries.insert(it)
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