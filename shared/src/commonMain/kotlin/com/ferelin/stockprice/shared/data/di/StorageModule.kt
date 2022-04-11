package com.ferelin.stockprice.shared.data.di

import com.ferelin.stockprice.db.StockPriceDb
import com.ferelin.stockprice.shared.data.entity.company.CompanyDao
import com.ferelin.stockprice.shared.data.entity.company.CompanyDaoImpl
import com.ferelin.stockprice.shared.data.entity.crypto.CryptoDao
import com.ferelin.stockprice.shared.data.entity.crypto.CryptoDaoImpl
import com.ferelin.stockprice.shared.data.entity.cryptoPrice.CryptoPriceDao
import com.ferelin.stockprice.shared.data.entity.cryptoPrice.CryptoPriceDaoImpl
import com.ferelin.stockprice.shared.data.entity.favouriteCompany.FavouriteCompanyDao
import com.ferelin.stockprice.shared.data.entity.favouriteCompany.FavouriteCompanyDaoImpl
import com.ferelin.stockprice.shared.data.entity.news.NewsDao
import com.ferelin.stockprice.shared.data.entity.news.NewsDaoImpl
import com.ferelin.stockprice.shared.data.entity.pastPrice.PastPriceDao
import com.ferelin.stockprice.shared.data.entity.pastPrice.PastPriceDaoImpl
import com.ferelin.stockprice.shared.data.entity.profile.ProfileDao
import com.ferelin.stockprice.shared.data.entity.profile.ProfileDaoImpl
import com.ferelin.stockprice.shared.data.entity.searchRequest.SearchRequestDao
import com.ferelin.stockprice.shared.data.entity.searchRequest.SearchRequestDaoImpl
import com.ferelin.stockprice.shared.data.entity.stockPrice.StockPriceDao
import com.ferelin.stockprice.shared.data.entity.stockPrice.StockPriceDaoImpl
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
}