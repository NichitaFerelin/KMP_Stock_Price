package com.ferelin.core.data.entity.cryptoPrice

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
internal interface CryptoPriceDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(cryptoPrices: List<CryptoPriceDBO>)

  @Query("SELECT * FROM `crypto_prices`")
  fun getAll(): Flow<List<CryptoPriceDBO>>
}

@Entity(tableName = CRYPTO_PRICES_DB_TABLE)
internal data class CryptoPriceDBO(
  @PrimaryKey
  val id: Int,
  val price: Double,
  val priceChange: Double,
  val priceChangePercents: Double
)

internal const val CRYPTO_PRICES_DB_TABLE = "crypto_prices"