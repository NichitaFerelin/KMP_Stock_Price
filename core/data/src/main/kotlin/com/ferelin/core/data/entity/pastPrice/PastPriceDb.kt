package com.ferelin.core.data.entity.pastPrice

import androidx.room.*
import io.reactivex.rxjava3.core.Observable

@Dao
internal interface PastPriceDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(pastPrices: List<PastPriceDBO>)

  @Query("SELECT * FROM `past_prices` WHERE companyId = :companyId")
  fun getAllBy(companyId: Int): Observable<List<PastPriceDBO>>

  @Query("DELETE FROM `past_prices` WHERE companyId = :companyId")
  fun eraseAllBy(companyId: Int)
}

@Entity(tableName = PAST_PRICE_DB_TABLE)
internal data class PastPriceDBO(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0L,
  val companyId: Int,
  val closePrice: Double,
  val dateMillis: Long
)

internal const val PAST_PRICE_DB_TABLE = "past_prices"