package com.ferelin.core.data.di

import com.ferelin.core.data.entity.company.CompanyDao
import com.ferelin.core.data.entity.company.CompanyDaoImpl
import com.ferelin.core.data.entity.company.CompanyJsonSource
import com.ferelin.core.data.entity.company.CompanyJsonSourceImpl
import com.ferelin.core.data.entity.companyNews.CompanyNewsDao
import com.ferelin.core.data.entity.companyNews.CompanyNewsDaoImpl
import com.ferelin.core.data.entity.crypto.CryptoDao
import com.ferelin.core.data.entity.crypto.CryptoDaoImpl
import com.ferelin.core.data.entity.crypto.CryptoJsonSource
import com.ferelin.core.data.entity.crypto.CryptoJsonSourceImpl
import com.ferelin.core.data.entity.cryptoPrice.CryptoPriceDao
import com.ferelin.core.data.entity.cryptoPrice.CryptoPriceDaoImpl
import com.ferelin.core.data.entity.marketNews.MarketNewsDao
import com.ferelin.core.data.entity.marketNews.MarketNewsDaoImpl
import com.ferelin.core.data.entity.searchRequest.SearchRequestDao
import com.ferelin.core.data.entity.searchRequest.SearchRequestDaoImpl
import com.ferelin.core.data.entity.stockPrice.StockPriceDao
import com.ferelin.core.data.entity.stockPrice.StockPriceDaoImpl
import com.ferelin.stockprice.db.StockPrice
import com.squareup.sqldelight.android.AndroidSqliteDriver
import com.squareup.sqldelight.db.SqlDriver
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val storageModule = module {
    single { StockPrice(get()) }

    single<SqlDriver> {
        AndroidSqliteDriver(
            StockPrice.Schema,
            androidContext(),
            DATABASE_NAME
        )
    }

    factory { get<StockPrice>().companyQueries }
    factory { get<StockPrice>().favoriteCompanyQueries }
    factory { get<StockPrice>().companyNewsQueries }
    factory { get<StockPrice>().cryptoQueries }
    factory { get<StockPrice>().cryptoPriceQueries }
    factory { get<StockPrice>().searchRequestQueries }
    factory { get<StockPrice>().stockPriceQueries }
    factory { get<StockPrice>().marketNewsQueries }

    factory<CompanyDao> { CompanyDaoImpl(get(), get()) }
    factory<CryptoDao> { CryptoDaoImpl(get()) }
    factory<CryptoPriceDao> { CryptoPriceDaoImpl(get()) }
    factory<CompanyNewsDao> { CompanyNewsDaoImpl(get()) }
    factory<SearchRequestDao> { SearchRequestDaoImpl(get()) }
    factory<StockPriceDao> { StockPriceDaoImpl(get()) }
    factory<MarketNewsDao> { MarketNewsDaoImpl(get()) }

    factory<CompanyJsonSource> { CompanyJsonSourceImpl(get()) }
    factory<CryptoJsonSource> { CryptoJsonSourceImpl(get()) }
}

internal const val DATABASE_NAME = "StockPrice"