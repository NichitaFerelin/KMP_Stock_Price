package com.ferelin.core.data.api

import com.ferelin.core.data.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import okhttp3.logging.HttpLoggingInterceptor.Level
import timber.log.Timber
import java.util.concurrent.TimeUnit

internal class OkHttpBuilder {
  fun build(): OkHttpClient {
    val loggingInterceptor = HttpLoggingInterceptor { message ->
      Timber.tag("OkHttp")
      Timber.d(message)
    }
    loggingInterceptor.level = HTTP_LOG_LEVEL
    return OkHttpClient.Builder()
      .connectTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
      .readTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
      .writeTimeout(HTTP_CONNECT_TIMEOUT, TimeUnit.MILLISECONDS)
      .addInterceptor(loggingInterceptor)
      .build()
  }
}

internal const val HTTP_CONNECT_TIMEOUT = 60_000L
internal val HTTP_LOG_LEVEL = if (BuildConfig.DEBUG) Level.BASIC else Level.NONE