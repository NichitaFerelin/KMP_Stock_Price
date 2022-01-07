package com.ferelin.core.data.entity.stockPrice

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
internal interface StockPriceDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insert(stockPriceDBO: StockPriceDBO)

  @Query("SELECT * FROM `stock_price`")
  fun getAll(): Flow<List<StockPriceDBO>>
}

@Entity(tableName = STOCK_PRICE_DB_TABLE)
internal data class StockPriceDBO(
  @PrimaryKey
  val id: Int,
  val currentPrice: Double,
  val previousClosePrice: Double,
  val openPrice: Double,
  val highPrice: Double,
  val lowPrice: Double
)

internal const val STOCK_PRICE_DB_TABLE = "stock_price"