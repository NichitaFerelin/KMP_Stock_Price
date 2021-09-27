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

import com.ferelin.domain.sources.LivePriceSource
import com.ferelin.domain.sources.NewsSource
import com.ferelin.domain.sources.PastPriceSource
import com.ferelin.domain.sources.StockPriceSource
import com.ferelin.remote.sources.LivePriceSourceImpl
import com.ferelin.remote.sources.NewsSourceImpl
import com.ferelin.remote.sources.PastPriceSourceImpl
import com.ferelin.remote.sources.StockPriceSourceImpl
import dagger.Binds
import dagger.Module

@Module
interface NetworkApiBindsModule {

    @Binds
    fun provideLivePriceSource(livePriceSourceImpl: LivePriceSourceImpl): LivePriceSource

    @Binds
    fun provideNewsSource(newsSourceImpl: NewsSourceImpl): NewsSource

    @Binds
    fun providePastPrice(pastPriceSourceImpl: PastPriceSourceImpl): PastPriceSource

    @Binds
    fun provideStockPriceSource(stockPriceSourceImpl: StockPriceSourceImpl): StockPriceSource
}