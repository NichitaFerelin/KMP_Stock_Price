package com.ferelin.core.data.api

import com.ferelin.core.data.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import timber.log.Timber
import java.util.concurrent.TimeUnit

internal class OkHttpBuilder(
  private val externalSettings: OkHttpClient.Builder.() -> OkHttpClient.Builder
) {
  fun build(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor { message ->
      Timber.tag("OkHttp")
      Timber.d(message)
    }
    loggingInterceptor.level = HTTP_LOG_LEVEL
    return OkHttpClient.Builder()
      .defaultOkHttp()
      .externalSettings()
      .build()
  }
}

internal class StocksTokenInterceptor(private val token: String) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    return chain.proceed(
      request = chain.request().addStocksToken(token)
    )
  }
}

internal class CryptosTokenInterceptor(private val token: String) : Interceptor {
  override fun intercept(chain: Interceptor.Chain): Response {
    return chain.proceed(
      request = chain.request().addCryptosToken(token)
    )
  }
}

internal fun Request.addStocksToken(token: String): Request {
  return this.newBuilder()
    .addHeader(STOCKS_TOKEN_HEADER, token)
    .build()
}

internal fun Request.addCryptosToken(token: String): Request {
  return this.newBuilder()
    .addHeader(CRYPTOS_TOKEN_HEADER, token)
    .build()
}

internal fun OkHttpClient.Builder.defaultOkHttp(): OkHttpClient.Builder {
  return this
    .addInterceptor(HttpLoggingInterceptor().setLevel(Level.BASIC))
    .connectTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
    .readTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
    .writeTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
}

internal const val STOCKS_TOKEN_HEADER = "token"
internal const val CRYPTOS_TOKEN_HEADER = "key"
internal const val HTTP_CONNECT_TIMEOUT = 60_000L
internal val HTTP_LOG_LEVEL = if (BuildConfig.RELEASE) Level.BASIC else Level.BODY