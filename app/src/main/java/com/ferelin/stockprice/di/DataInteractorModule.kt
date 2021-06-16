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

package com.ferelin.stockprice.di

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import androidx.room.Room
import com.ferelin.local.companiesDb.CompaniesDao
import com.ferelin.local.companiesDb.CompaniesDatabase
import com.ferelin.local.messagesDb.MessagesDao
import com.ferelin.local.messagesDb.MessagesDatabase
import com.ferelin.remote.utils.Api
import com.ferelin.repository.Repository
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
class DataInteractorModule {

    @Provides
    @Singleton
    fun provideCompaniesDatabase(context: Context): CompaniesDatabase {
        return Room.databaseBuilder(
            context,
            CompaniesDatabase::class.java,
            CompaniesDatabase.DB_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideCompaniesDao(database: CompaniesDatabase): CompaniesDao {
        return database.companiesDao()
    }

    @Provides
    @Singleton
    fun provideMessagesDatabase(context: Context): MessagesDatabase {
        return Room.databaseBuilder(
            context,
            MessagesDatabase::class.java,
            MessagesDatabase.DB_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideMessagesDao(database: MessagesDatabase): MessagesDao {
        return database.messagedDao()
    }

    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        val moshi = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()

        val httpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC))
            .build()

        return Retrofit.Builder()
            .baseUrl(Api.FINNHUB_BASE_URL)
            .client(httpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
    }

    @Provides
    fun provideConnectivityManager(context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }

    @Provides
    fun provideNetworkRequest(): NetworkRequest {
        return NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()
    }

    @Provides
    @Named("isUserLogged")
    fun provideUserLogState(repository: Repository): Boolean {
        return repository.provideIsUserLogged()
    }
}