package com.ferelin.stockprice.data.di

import com.ferelin.stockprice.data.entity.cryptoPrice.CryptoPriceApi
import com.ferelin.stockprice.data.entity.cryptoPrice.CryptoPriceApiImpl
import com.ferelin.stockprice.data.entity.news.NewsApi
import com.ferelin.stockprice.data.entity.news.NewsApiImpl
import com.ferelin.stockprice.data.entity.pastPrice.PastPriceApi
import com.ferelin.stockprice.data.entity.pastPrice.PastPriceApiImpl
import com.ferelin.stockprice.data.entity.stockPrice.StockPriceApi
import com.ferelin.stockprice.data.entity.stockPrice.StockPriceApiImpl
import io.ktor.client.*
import io.ktor.client.engine.*
import io.ktor.client.features.json.*
import io.ktor.client.features.json.serializer.*
import io.ktor.client.features.logging.*
import org.koin.core.qualifier.named
import org.koin.dsl.module

internal val apiModule = module {
  single { buildHttpClient(get()) }

  factory(qualifier = named(NAMED_STOCKS_TOKEN)) { "temp" }
  factory(qualifier = named(NAMED_CRYPTOS_TOKEN)) { "temp" }

  factory<CryptoPriceApi> { CryptoPriceApiImpl(get()) }
  factory<NewsApi> { NewsApiImpl(get()) }
  factory<PastPriceApi> { PastPriceApiImpl(get()) }
  factory<StockPriceApi> { StockPriceApiImpl(get()) }
}

internal const val NAMED_STOCKS_TOKEN = "stocks_token"
internal const val NAMED_CRYPTOS_TOKEN = "cryptos_token"

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
      // level = if (BuildConfig.DEBUG) LogLevel.ALL else LogLevel.INFO
    }
  }
}