package com.ferelin.core.data.entity.company

import androidx.room.*
import io.reactivex.rxjava3.core.Observable

@Dao
internal interface CompanyDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(companiesDBO: List<CompanyDBO>)

  @Query("SELECT * FROM `companies`")
  fun getAll(): Observable<List<CompanyDBO>>
}

@Entity(tableName = COMPANY_DB_TABLE)
internal data class CompanyDBO(
  @PrimaryKey
  val id: Int,
  val name: String,
  val ticker: String,
  val logoUrl: String
)

internal const val COMPANY_DB_TABLE = "companies"