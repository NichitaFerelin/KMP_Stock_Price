package com.ferelin.data_network_api.builder

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

object RetrofitBuilder {

    const val NOMICS_BASE_URL = "https://api.nomics.com/v1/"
    const val FINNHUB_BASE_URL = "https://finnhub.io/api/v1/"

    fun build(baseUrl: String) : Retrofit {
        val moshi = Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .build()

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }
}