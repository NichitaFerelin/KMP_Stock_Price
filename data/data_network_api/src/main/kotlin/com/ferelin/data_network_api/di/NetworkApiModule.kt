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

package com.ferelin.data_network_api.di

import android.content.Context
import com.ferelin.data_network_api.R
import com.ferelin.data_network_api.builder.RetrofitBuilder
import com.ferelin.data_network_api.entities.CryptoPriceApi
import com.ferelin.data_network_api.entities.NewsApi
import com.ferelin.data_network_api.entities.PastPricesApi
import com.ferelin.data_network_api.entities.StockPriceApi
import com.ferelin.shared.NAMED_CRYPTO_RETROFIT
import com.ferelin.shared.NAMED_CRYPTO_TOKEN
import com.ferelin.shared.NAMED_STOCKS_RETROFIT
import com.ferelin.shared.NAMED_STOCKS_TOKEN
import dagger.Module
import dagger.Provides
import retrofit2.Retrofit
import javax.inject.Named
import javax.inject.Singleton

@Module
class NetworkApiModule {

    @Named(NAMED_STOCKS_RETROFIT)
    @Singleton
    @Provides
    fun provideStocksRetrofit(): Retrofit {
        return RetrofitBuilder.build(RetrofitBuilder.FINNHUB_BASE_URL)
    }

    @Named(NAMED_CRYPTO_RETROFIT)
    @Singleton
    @Provides
    fun provideCryptoRetrofit(): Retrofit {
        return RetrofitBuilder.build(RetrofitBuilder.NOMICS_BASE_URL)
    }

    @Provides
    fun provideNewsApi(@Named(NAMED_STOCKS_RETROFIT) retrofit: Retrofit): NewsApi {
        return retrofit.create(NewsApi::class.java)
    }

    @Provides
    fun provideStockPriceApi(@Named(NAMED_STOCKS_RETROFIT) retrofit: Retrofit): StockPriceApi {
        return retrofit.create(StockPriceApi::class.java)
    }

    @Provides
    fun providePastPriceApi(@Named(NAMED_STOCKS_RETROFIT) retrofit: Retrofit): PastPricesApi {
        return retrofit.create(PastPricesApi::class.java)
    }

    @Provides
    fun provideCryptoPriceApi(@Named(NAMED_CRYPTO_RETROFIT) retrofit: Retrofit): CryptoPriceApi {
        return retrofit.create(CryptoPriceApi::class.java)
    }

    @Named(NAMED_STOCKS_TOKEN)
    @Provides
    fun provideFinnhubToken(context: Context): String {
        return context.resources.getString(R.string.api_finnhub_token)
    }

    @Named(NAMED_CRYPTO_TOKEN)
    @Provides
    fun provideCryptoToken(context: Context): String {
        return context.resources.getString(R.string.api_nomics_token)
    }
}