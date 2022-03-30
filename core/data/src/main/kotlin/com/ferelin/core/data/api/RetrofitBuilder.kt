package com.ferelin.core.data.api

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory

internal class RetrofitBuilder(
  private val baseUrl: String,
  private val httpClient: OkHttpClient,
  private val moshi: Moshi
) {
  fun build(): Retrofit {
    return Retrofit.Builder()
      .baseUrl(baseUrl)
      .client(httpClient)
      .addConverterFactory(MoshiConverterFactory.create(moshi))
      .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
      .build()
  }
}

internal const val STOCKS_BASE_URL = "https://finnhub.io/api/v1/"
internal const val STOCKS_TOKEN = "stocks-token"
internal const val CRYPTOS_BASE_URL = "https://api.nomics.com/v1/"
internal const val CRYPTOS_TOKEN = "cryptos-token"