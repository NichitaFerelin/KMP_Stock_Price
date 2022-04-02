package com.ferelin.core.data.di

import android.content.Context
import com.ferelin.core.data.entity.company.CompanyDao
import com.ferelin.core.data.entity.company.CompanyDaoImpl
import com.ferelin.core.data.entity.company.CompanyJsonSource
import com.ferelin.core.data.entity.company.CompanyJsonSourceImpl
import com.ferelin.core.data.entity.crypto.CryptoDao
import com.ferelin.core.data.entity.crypto.CryptoDaoImpl
import com.ferelin.core.data.entity.crypto.CryptoJsonSource
import com.ferelin.core.data.entity.crypto.CryptoJsonSourceImpl
import com.ferelin.core.data.entity.cryptoPrice.CryptoPriceDao
import com.ferelin.core.data.entity.cryptoPrice.CryptoPriceDaoImpl
import com.ferelin.core.data.entity.favouriteCompany.FavouriteCompanyDao
import com.ferelin.core.data.entity.favouriteCompany.FavouriteCompanyDaoImpl
import com.ferelin.core.data.entity.news.NewsDao
import com.ferelin.core.data.entity.news.NewsDaoImpl
import com.ferelin.core.data.entity.pastPrice.PastPriceDao
import com.ferelin.core.data.entity.pastPrice.PastPriceDaoImpl
import com.ferelin.core.data.entity.profile.ProfileDao
import com.ferelin.core.data.entity.profile.ProfileDaoImpl
import com.ferelin.core.data.entity.searchRequest.SearchRequestDao
import com.ferelin.core.data.entity.searchRequest.SearchRequestDaoImpl
import com.ferelin.core.data.entity.stockPrice.StockPriceDao
import com.ferelin.core.data.entity.stockPrice.StockPriceDaoImpl
import com.ferelin.stockprice.StockPriceDb
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import dagger.Binds
import dagger.Module
import dagger.Provides
import stockprice.*
import javax.inject.Singleton

@Module(
  includes = [
    StorageModuleBinds::class,
    RepositoryModuleBinds::class
  ]
)
class StorageModule {
  @Provides
  @Singleton
  fun appDatabase(driver: SqlDriver): StockPriceDb {
    return StockPriceDb(driver)
  }

  @Provides
  @Singleton
  fun sqlDriver(context: Context): SqlDriver {
    return AndroidSqliteDriver(StockPriceDb.Schema, context, "StockPriceDb")
  }

  @Provides
  fun companyQueries(stockPriceDb: StockPriceDb): CompanyQueries {
    return stockPriceDb.companyQueries
  }

  @Provides
  fun cryptoQueries(stockPriceDb: StockPriceDb): CryptoQueries {
    return stockPriceDb.cryptoQueries
  }

  @Provides
  fun cryptoPriceQueries(stockPriceDb: StockPriceDb): CryptoPriceQueries {
    return stockPriceDb.cryptoPriceQueries
  }

  @Provides
  fun favouriteCompanyQueries(stockPriceDb: StockPriceDb): FavouriteCompanyQueries {
    return stockPriceDb.favouriteCompanyQueries
  }

  @Provides
  fun newsQueries(stockPriceDb: StockPriceDb): NewsQueries {
    return stockPriceDb.newsQueries
  }

  @Provides
  fun pastPriceQueries(stockPriceDb: StockPriceDb): PastPriceQueries {
    return stockPriceDb.pastPriceQueries
  }

  @Provides
  fun profileQueries(stockPriceDb: StockPriceDb): ProfileQueries {
    return stockPriceDb.profileQueries
  }

  @Provides
  fun searchRequestQueries(stockPriceDb: StockPriceDb): SearchRequestQueries {
    return stockPriceDb.searchRequestQueries
  }

  @Provides
  fun stockPriceQueries(stockPriceDb: StockPriceDb): StockPriceQueries {
    return stockPriceDb.stockPriceQueries
  }
}

@Suppress("unused")
@Module
internal interface StorageModuleBinds {
  @Binds
  fun companyJsonSource(impl: CompanyJsonSourceImpl): CompanyJsonSource

  @Binds
  fun cryptoJsonSource(impl: CryptoJsonSourceImpl): CryptoJsonSource

  @Binds
  fun companyDao(impl: CompanyDaoImpl): CompanyDao

  @Binds
  fun cryptoDao(impl: CryptoDaoImpl): CryptoDao

  @Binds
  fun cryptoPriceDao(impl: CryptoPriceDaoImpl): CryptoPriceDao

  @Binds
  fun favouriteCompanyDao(impl: FavouriteCompanyDaoImpl): FavouriteCompanyDao

  @Binds
  fun newsDao(impl: NewsDaoImpl): NewsDao

  @Binds
  fun pastPriceDao(impl: PastPriceDaoImpl): PastPriceDao

  @Binds
  fun profileDao(impl: ProfileDaoImpl): ProfileDao

  @Binds
  fun searchRequestDao(impl: SearchRequestDaoImpl): SearchRequestDao

  @Binds
  fun stockPriceDao(impl: StockPriceDaoImpl): StockPriceDao
}
