package com.ferelin.core.data.entity.favouriteCompany

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
internal interface FavouriteCompanyDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(favouriteCompanyDBO: FavouriteCompanyDBO)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(companies: List<FavouriteCompanyDBO>)

  @Query("SELECT * FROM `favourite_companies`")
  fun getAll(): Flow<List<FavouriteCompanyDBO>>

  @Delete
  suspend fun erase(favouriteCompanyDBO: FavouriteCompanyDBO)

  @Query("DELETE FROM `favourite_companies`")
  suspend fun eraseAll()
}

@Entity(tableName = FAVOURITE_COMPANIES_DB_TABLE)
internal data class FavouriteCompanyDBO(
  @PrimaryKey
  val id: Int
)

internal const val FAVOURITE_COMPANIES_DB_TABLE = "favourite_companies"