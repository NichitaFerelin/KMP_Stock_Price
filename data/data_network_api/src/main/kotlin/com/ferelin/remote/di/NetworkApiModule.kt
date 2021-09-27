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
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named

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
        return " " // TODO
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