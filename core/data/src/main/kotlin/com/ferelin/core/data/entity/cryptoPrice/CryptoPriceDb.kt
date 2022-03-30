package com.ferelin.core.data.entity.cryptoPrice

import androidx.room.*
import io.reactivex.rxjava3.core.Observable

@Dao
internal interface CryptoPriceDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(cryptoPrices: List<CryptoPriceDBO>)

  @Query("SELECT * FROM `crypto_prices`")
  fun getAll(): Observable<List<CryptoPriceDBO>>
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