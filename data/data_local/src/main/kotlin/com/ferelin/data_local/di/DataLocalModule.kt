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

package com.ferelin.data_local.di

import android.content.Context
import com.ferelin.data_local.database.*
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class DataLocalModule {

    @Singleton
    @Provides
    fun provideAppDatabase(context: Context): AppDatabase {
        return AppDatabase.buildDatabase(context)
    }

    @Provides
    fun provideCompaniesDao(appDatabase: AppDatabase): CompaniesDao {
        return appDatabase.companyDao()
    }

    @Provides
    fun provideNewsDao(appDatabase: AppDatabase): NewsDao {
        return appDatabase.newsDao()
    }

    @Provides
    fun providePastPriceDao(appDatabase: AppDatabase): PastPriceDao {
        return appDatabase.pastPriceDao()
    }

    @Provides
    fun provideProfileDao(appDatabase: AppDatabase): ProfileDao {
        return appDatabase.profileDao()
    }

    @Provides
    fun provideStockPriceDao(appDatabase: AppDatabase): StockPriceDao {
        return appDatabase.stockPriceDao()
    }

    @Provides
    fun provideSearchRequestsDao(appDatabase: AppDatabase): SearchRequestsDao {
        return appDatabase.searchRequestsDao()
    }
}