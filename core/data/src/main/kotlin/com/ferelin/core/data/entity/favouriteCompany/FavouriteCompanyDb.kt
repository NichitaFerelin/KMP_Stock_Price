package com.ferelin.core.data.entity.favouriteCompany

import androidx.room.*
import io.reactivex.rxjava3.core.Observable

@Dao
internal interface FavouriteCompanyDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insert(favouriteCompanyDBO: FavouriteCompanyDBO)

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(companies: List<FavouriteCompanyDBO>)

  @Query("SELECT * FROM `favourite_companies`")
  fun getAll(): Observable<List<FavouriteCompanyDBO>>

  @Delete
  fun erase(favouriteCompanyDBO: FavouriteCompanyDBO)

  @Query("DELETE FROM `favourite_companies`")
  fun eraseAll()
}

@Entity(tableName = FAVOURITE_COMPANIES_DB_TABLE)
internal data class FavouriteCompanyDBO(
  @PrimaryKey
  val id: Int
)

internal const val FAVOURITE_COMPANIES_DB_TABLE = "favourite_companies"