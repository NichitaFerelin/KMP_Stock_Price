/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ferelin.remote.di

import com.ferelin.remote.entities.NewsApi
import com.ferelin.remote.entities.PastPricesApi
import com.ferelin.remote.entities.StockPriceApi
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
class NetworkApiModule {

    @Provides
    @Named("FinnhubWebSocketUrl")
    fun provideFinnhubWebSocketUrl(): String {
        return "wss://ws.finnhub.io?token="
    }

    @Provides
    @Named("FinnhubToken")
    fun provideFinnhubToken(): String {
        return "" // TODO
    }

    @Provides
    @Named("FinnhubBaseUrl")
    fun provideFinnhubBaseUrl(): String {
        return "https://finnhub.io/api/v1/"
    }

    /*
    *
    * @Provides
    @Named("FinnhubToken")
    fun provideToken(context: Context): String {
        // Set your own api key to local.properties root file. Name value as 'apiKey'.
        return context.resources.getString(R.string.api_key)
    }
    * */

    @Provides
    @Singleton
    fun provideRetrofit(@Named("FinnhubBaseUrl") finnhubUrl: String): Retrofit {
        val moshi = Moshi
            .Builder()
            .add(KotlinJsonAdapterFactory())
            .build()

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .build()

        return Retrofit.Builder()
            .baseUrl(finnhubUrl)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    fun provideNewsApi(retrofit: Retrofit): NewsApi {
        return retrofit.create(NewsApi::class.java)
    }

    @Provides
    fun provideStockPriceApi(retrofit: Retrofit): StockPriceApi {
        return retrofit.create(StockPriceApi::class.java)
    }

    @Provides
    fun providePastPriceApi(retrofit: Retrofit): PastPricesApi {
        return retrofit.create(PastPricesApi::class.java)
    }
}