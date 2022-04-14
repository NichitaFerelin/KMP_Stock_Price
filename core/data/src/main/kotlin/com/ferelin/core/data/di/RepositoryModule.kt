package com.ferelin.core.data.di

import com.ferelin.core.data.repository.*
import com.ferelin.core.domain.repository.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

val repositoryModule = module {
    factory<CryptoPriceRepository> {
        CryptoPriceRepositoryImpl(get(), get(), get(named(NAMED_CRYPTOS_TOKEN)))
    }
    factory<NewsRepository> {
        NewsRepositoryImpl(get(), get(), get(named(NAMED_STOCKS_TOKEN)))
    }
    factory<PastPriceRepository> {
        PastPriceRepositoryImpl(get(), get(), get(named(NAMED_STOCKS_TOKEN)))
    }
    factory<StockPriceRepository> {
        StockPriceRepositoryImpl(get(), get(), get(named(NAMED_STOCKS_TOKEN)))
    }

    factory<CompanyRepository> { CompanyRepositoryImpl(get(), get()) }
    factory<CryptoRepository> { CryptoRepositoryImpl(get(), get()) }
    factory<SearchRequestsRepository> { SearchRequestsRepositoryImpl(get()) }
}