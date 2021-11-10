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

import com.ferelin.data_network_api.sources.*
import com.ferelin.domain.sources.*
import dagger.Binds
import dagger.Module

@Module
interface NetworkApiBindsModule {

    @Binds
    fun provideLivePriceSource(livePriceSourceImpl: LivePriceSourceImpl): LivePriceSource

    @Binds
    fun provideNewsSource(newsSourceImpl: NewsSourceImpl): NewsSource

    @Binds
    fun providePastPriceSource(pastPriceSourceImpl: PastPriceSourceImpl): PastPriceSource

    @Binds
    fun provideStockPriceSource(stockPriceSourceImpl: StockPriceSourceImpl): StockPriceSource

    @Binds
    fun provideCryptoPriceSource(cryptoPriceSourceImpl: CryptoPriceSourceImpl) : CryptoPriceSource
}