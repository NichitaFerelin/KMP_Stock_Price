package com.ferelin.core.data.di

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
import com.ferelin.core.data.storage.PreferencesProvider
import com.ferelin.stockprice.StockPrice
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val storageModule = module {
  single { StockPrice(get()) }
  single { PreferencesProvider(get()) }

  single<SqlDriver> {
    AndroidSqliteDriver(
      StockPrice.Schema,
      androidContext(),
      DATABASE_NAME
    )
  }

  factory { get<StockPrice>().companyQueries }
  factory { get<StockPrice>().newsQueries }
  factory { get<StockPrice>().cryptoQueries }
  factory { get<StockPrice>().cryptoPriceQueries }
  factory { get<StockPrice>().favouriteCompanyQueries }
  factory { get<StockPrice>().pastPriceQueries }
  factory { get<StockPrice>().profileQueries }
  factory { get<StockPrice>().searchRequestQueries }
  factory { get<StockPrice>().stockPriceQueries }

  factory<CompanyDao> { CompanyDaoImpl(get()) }
  factory<CryptoDao> { CryptoDaoImpl(get()) }
  factory<CryptoPriceDao> { CryptoPriceDaoImpl(get()) }
  factory<FavouriteCompanyDao> { FavouriteCompanyDaoImpl(get()) }
  factory<NewsDao> { NewsDaoImpl(get()) }
  factory<PastPriceDao> { PastPriceDaoImpl(get()) }
  factory<ProfileDao> { ProfileDaoImpl(get()) }
  factory<SearchRequestDao> { SearchRequestDaoImpl(get()) }
  factory<StockPriceDao> { StockPriceDaoImpl(get()) }
  factory<CompanyJsonSource> { CompanyJsonSourceImpl(get(), get()) }
  factory<CryptoJsonSource> { CryptoJsonSourceImpl(get(), get()) }
}

internal const val DATABASE_NAME = "StockPrice"