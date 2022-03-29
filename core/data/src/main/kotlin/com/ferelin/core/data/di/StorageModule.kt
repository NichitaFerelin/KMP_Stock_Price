package com.ferelin.core.data.di

import android.content.Context
import com.ferelin.core.data.entity.company.CompanyDao
import com.ferelin.core.data.entity.company.CompanyJsonSource
import com.ferelin.core.data.entity.company.CompanyJsonSourceImpl
import com.ferelin.core.data.entity.crypto.CryptoDao
import com.ferelin.core.data.entity.crypto.CryptoJsonSource
import com.ferelin.core.data.entity.crypto.CryptoJsonSourceImpl
import com.ferelin.core.data.entity.cryptoPrice.CryptoPriceDao
import com.ferelin.core.data.entity.favouriteCompany.FavouriteCompanyDao
import com.ferelin.core.data.entity.news.NewsDao
import com.ferelin.core.data.entity.pastPrice.PastPriceDao
import com.ferelin.core.data.entity.profile.ProfileDao
import com.ferelin.core.data.entity.searchRequest.SearchRequestDao
import com.ferelin.core.data.entity.stockPrice.StockPriceDao
import com.ferelin.core.data.storage.AppDatabase
import dagger.Binds
import dagger.Module
import dagger.Provides
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
  internal fun appDatabase(context: Context): AppDatabase {
    return AppDatabase.buildDatabase(context)
  }

  @Provides
  internal fun companyDao(appDatabase: AppDatabase): CompanyDao {
    return appDatabase.companyDao()
  }

  @Provides
  internal fun cryptoDao(appDatabase: AppDatabase): CryptoDao {
    return appDatabase.cryptoDao()
  }

  @Provides
  internal fun cryptoPriceDao(appDatabase: AppDatabase): CryptoPriceDao {
    return appDatabase.cryptoPriceDao()
  }

  @Provides
  internal fun favouriteCompanyDao(appDatabase: AppDatabase): FavouriteCompanyDao {
    return appDatabase.favouriteCompanyDao()
  }

  @Provides
  internal fun newsDao(appDatabase: AppDatabase): NewsDao {
    return appDatabase.newsDao()
  }

  @Provides
  internal fun pastPriceDao(appDatabase: AppDatabase): PastPriceDao {
    return appDatabase.pastPriceDao()
  }

  @Provides
  internal fun profileDao(appDatabase: AppDatabase): ProfileDao {
    return appDatabase.profileDao()
  }

  @Provides
  internal fun searchRequestDao(appDatabase: AppDatabase): SearchRequestDao {
    return appDatabase.searchRequestDao()
  }

  @Provides
  internal fun stockPriceDao(appDatabase: AppDatabase): StockPriceDao {
    return appDatabase.stockPriceDao()
  }
}

@Suppress("unused")
@Module
internal interface StorageModuleBinds {
  @Binds
  fun companyJsonSource(impl: CompanyJsonSourceImpl): CompanyJsonSource

  @Binds
  fun cryptoJsonSource(impl: CryptoJsonSourceImpl): CryptoJsonSource
}
