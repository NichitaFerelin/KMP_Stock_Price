package com.ferelin.core.data.storage

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.ferelin.core.data.entity.company.CompanyDBO
import com.ferelin.core.data.entity.company.CompanyDao
import com.ferelin.core.data.entity.crypto.CryptoDBO
import com.ferelin.core.data.entity.crypto.CryptoDao
import com.ferelin.core.data.entity.cryptoPrice.CryptoPriceDBO
import com.ferelin.core.data.entity.cryptoPrice.CryptoPriceDao
import com.ferelin.core.data.entity.favouriteCompany.FavouriteCompanyDBO
import com.ferelin.core.data.entity.favouriteCompany.FavouriteCompanyDao
import com.ferelin.core.data.entity.news.NewsDBO
import com.ferelin.core.data.entity.news.NewsDao
import com.ferelin.core.data.entity.pastPrice.PastPriceDBO
import com.ferelin.core.data.entity.pastPrice.PastPriceDao
import com.ferelin.core.data.entity.profile.ProfileDBO
import com.ferelin.core.data.entity.profile.ProfileDao
import com.ferelin.core.data.entity.searchRequest.SearchRequestDBO
import com.ferelin.core.data.entity.searchRequest.SearchRequestDao
import com.ferelin.core.data.entity.stockPrice.StockPriceDBO
import com.ferelin.core.data.entity.stockPrice.StockPriceDao

@Database(
  entities = [
    CompanyDBO::class,
    CryptoDBO::class,
    CryptoPriceDBO::class,
    FavouriteCompanyDBO::class,
    NewsDBO::class,
    PastPriceDBO::class,
    ProfileDBO::class,
    SearchRequestDBO::class,
    StockPriceDBO::class
  ], version = 3
)
internal abstract class AppDatabase : RoomDatabase() {
  abstract fun companyDao(): CompanyDao
  abstract fun cryptoDao(): CryptoDao
  abstract fun cryptoPriceDao(): CryptoPriceDao
  abstract fun favouriteCompanyDao(): FavouriteCompanyDao
  abstract fun newsDao(): NewsDao
  abstract fun pastPriceDao(): PastPriceDao
  abstract fun profileDao(): ProfileDao
  abstract fun searchRequestDao(): SearchRequestDao
  abstract fun stockPriceDao(): StockPriceDao

  companion object {
    fun buildDatabase(context: Context): AppDatabase {
      return Room
        .databaseBuilder(context, AppDatabase::class.java, DATABASE_NAME)
        .addMigrations(
          AppDatabaseMigrations.migration_1_to_2,
          AppDatabaseMigrations.migration_2_to_3
        )
        .build()
    }
  }
}

internal const val DATABASE_NAME = "stock.price.db"