package com.ferelin.stockprice.shared.commonMain.data.di

import com.ferelin.stockprice.shared.commonMain.data.entity.cryptoPrice.CryptoPriceApi
import com.ferelin.stockprice.shared.commonMain.data.entity.cryptoPrice.CryptoPriceApiImpl
import com.ferelin.stockprice.shared.commonMain.data.entity.news.NewsApi
import com.ferelin.stockprice.shared.commonMain.data.entity.news.NewsApiImpl
import com.ferelin.stockprice.shared.commonMain.data.entity.pastPrice.PastPriceApi
import com.ferelin.stockprice.shared.commonMain.data.entity.pastPrice.PastPriceApiImpl
import com.ferelin.stockprice.shared.commonMain.data.entity.stockPrice.StockPriceApi
import com.ferelin.stockprice.shared.commonMain.data.entity.stockPrice.StockPriceApiImpl
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val apiModule = module {
  single { buildHttpClient(get()) }

  factory(qualifier = named(NAMED_STOCKS_TOKEN)) { PUBLIC_DEBUG_STOCKS_TOKEN }
  factory(qualifier = named(NAMED_CRYPTOS_TOKEN)) { PUBLIC_DEBUG_CRYPTOS_TOKEN }

  factory<CryptoPriceApi> { CryptoPriceApiImpl(get()) }
  factory<NewsApi> { NewsApiImpl(get()) }
  factory<PastPriceApi> { PastPriceApiImpl(get()) }
  factory<StockPriceApi> { StockPriceApiImpl(get()) }
}

internal const val NAMED_STOCKS_TOKEN = "stocks_token"
internal const val NAMED_CRYPTOS_TOKEN = "cryptos_token"

private const val PUBLIC_DEBUG_STOCKS_TOKEN = "c5n906iad3ido15tstu0"
private const val PUBLIC_DEBUG_CRYPTOS_TOKEN = "cb99d1ebf28482d6fb54f7c9002319aea14401c7"

private fun buildHttpClient(engine: HttpClientEngine): HttpClient {
  return HttpClient(engine) {
    install(JsonFeature) {
      serializer = KotlinxSerializer(
        json = kotlinx.serialization.json.Json {
          ignoreUnknownKeys = true
        }
      )
    }
    install(Logging) {
      logger = Logger.DEFAULT
      level = LogLevel.INFO
    }
  }
}