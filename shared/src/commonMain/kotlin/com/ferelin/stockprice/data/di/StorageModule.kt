package com.ferelin.stockprice.data.di

import com.ferelin.stockprice.data.entity.company.CompanyJsonSource
import com.ferelin.stockprice.data.entity.company.CompanyJsonSourceImpl
import com.ferelin.stockprice.data.entity.company.CompanyDao
import com.ferelin.stockprice.data.entity.company.CompanyDaoImpl
import com.ferelin.stockprice.data.entity.crypto.CryptoDao
import com.ferelin.stockprice.data.entity.crypto.CryptoDaoImpl
import com.ferelin.stockprice.data.entity.crypto.CryptoJsonSource
import com.ferelin.stockprice.data.entity.crypto.CryptoJsonSourceImpl
import com.ferelin.stockprice.data.entity.cryptoPrice.CryptoPriceDao
import com.ferelin.stockprice.data.entity.cryptoPrice.CryptoPriceDaoImpl
import com.ferelin.stockprice.data.entity.favouriteCompany.FavouriteCompanyDao
import com.ferelin.stockprice.data.entity.favouriteCompany.FavouriteCompanyDaoImpl
import com.ferelin.stockprice.data.entity.news.NewsDao
import com.ferelin.stockprice.data.entity.news.NewsDaoImpl
import com.ferelin.stockprice.data.entity.pastPrice.PastPriceDao
import com.ferelin.stockprice.data.entity.pastPrice.PastPriceDaoImpl
import com.ferelin.stockprice.data.entity.profile.ProfileDao
import com.ferelin.stockprice.data.entity.profile.ProfileDaoImpl
import com.ferelin.stockprice.data.entity.searchRequest.SearchRequestDao
import com.ferelin.stockprice.data.entity.searchRequest.SearchRequestDaoImpl
import com.ferelin.stockprice.data.entity.stockPrice.StockPriceDao
import com.ferelin.stockprice.data.entity.stockPrice.StockPriceDaoImpl
import com.ferelin.stockprice.db.StockPriceDb
import org.koin.dsl.module

internal val storageModule = module {
  single { StockPriceDb(get()) }
  factory { get<StockPriceDb>().companyQueries }
  factory { get<StockPriceDb>().newsQueries }
  factory { get<StockPriceDb>().cryptoQueries }
  factory { get<StockPriceDb>().cryptoPriceQueries }
  factory { get<StockPriceDb>().favouriteCompanyQueries }
  factory { get<StockPriceDb>().pastPriceQueries }
  factory { get<StockPriceDb>().profileQueries }
  factory { get<StockPriceDb>().searchRequestQueries }
  factory { get<StockPriceDb>().stockPriceQueries }

  factory<CompanyDao> { CompanyDaoImpl(get()) }
  factory<CryptoDao> { CryptoDaoImpl(get()) }
  factory<CryptoPriceDao> { CryptoPriceDaoImpl(get()) }
  factory<FavouriteCompanyDao> { FavouriteCompanyDaoImpl(get()) }
  factory<NewsDao> { NewsDaoImpl(get()) }
  factory<PastPriceDao> { PastPriceDaoImpl(get()) }
  factory<ProfileDao> { ProfileDaoImpl(get()) }
  factory<SearchRequestDao> { SearchRequestDaoImpl(get()) }
  factory<StockPriceDao> { StockPriceDaoImpl(get()) }

  factory<CompanyJsonSource> { CompanyJsonSourceImpl() }
  factory<CryptoJsonSource> { CryptoJsonSourceImpl() }
}