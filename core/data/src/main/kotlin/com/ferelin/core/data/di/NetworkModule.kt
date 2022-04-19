package com.ferelin.core.data.di

import com.ferelin.core.data.R
import com.ferelin.core.data.api.buildKtorHttpClient
import com.ferelin.core.data.entity.companyNews.CompanyNewsApi
import com.ferelin.core.data.entity.companyNews.CompanyNewsApiImpl
import com.ferelin.core.data.entity.cryptoPrice.CryptoPriceApi
import com.ferelin.core.data.entity.cryptoPrice.CryptoPriceApiImpl
import com.ferelin.core.data.entity.marketNews.MarketNewsApi
import com.ferelin.core.data.entity.marketNews.MarketNewsApiImpl
import com.ferelin.core.data.entity.stockPrice.StockPriceApi
import com.ferelin.core.data.entity.stockPrice.StockPriceApiImpl
import org.koin.android.ext.koin.androidContext
import org.koin.core.qualifier.named
import org.koin.dsl.module

val networkModule = module {
    single { buildKtorHttpClient() }

    factory(
        qualifier = named(NAMED_STOCKS_TOKEN)
    ) { androidContext().resources.getString(R.string.api_finnhub_token) }

    factory(
        qualifier = named(NAMED_CRYPTOS_TOKEN)
    ) { androidContext().resources.getString(R.string.api_nomics_token) }

    factory<CryptoPriceApi> { CryptoPriceApiImpl(get()) }
    factory<CompanyNewsApi> { CompanyNewsApiImpl(get()) }
    factory<StockPriceApi> { StockPriceApiImpl(get()) }
    factory<MarketNewsApi> { MarketNewsApiImpl(get()) }
}

internal const val NAMED_STOCKS_TOKEN = "stocks_token"
internal const val NAMED_CRYPTOS_TOKEN = "cryptos_token"