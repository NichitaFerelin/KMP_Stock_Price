package com.ferelin.core.data.entity.crypto

import androidx.room.*
import io.reactivex.rxjava3.core.Observable

@Dao
internal interface CryptoDao {
  @Insert(onConflict = OnConflictStrategy.REPLACE)
  fun insertAll(cryptosDBO: List<CryptoDBO>)

  @Query("SELECT * FROM `crypto`")
  fun getAll(): Observable<List<CryptoDBO>>
}

@Entity(tableName = CRYPTO_DB_TABLE)
internal data class CryptoDBO(
  @PrimaryKey
  val id: Int,
  val ticker: String,
  val name: String,
  val logoUrl: String
)

internal const val CRYPTO_DB_TABLE = "crypto"