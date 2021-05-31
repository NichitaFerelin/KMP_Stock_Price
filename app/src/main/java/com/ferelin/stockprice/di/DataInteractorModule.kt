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
import com.ferelin.local.LocalManager
import com.ferelin.local.LocalManagerHelper
import com.ferelin.local.database.CompaniesDatabase
import com.ferelin.local.database.CompaniesManager
import com.ferelin.local.database.CompaniesManagerHelper
import com.ferelin.local.json.JsonManager
import com.ferelin.local.json.JsonManagerHelper
import com.ferelin.local.preferences.StorePreferences
import com.ferelin.local.preferences.StorePreferencesHelper
import com.ferelin.remote.RemoteMediator
import com.ferelin.remote.RemoteMediatorHelper
import com.ferelin.remote.network.NetworkManager
import com.ferelin.remote.network.NetworkManagerHelper
import com.ferelin.remote.webSocket.WebSocketConnector
import com.ferelin.remote.webSocket.WebSocketConnectorHelper
import com.ferelin.repository.RepositoryManager
import com.ferelin.repository.RepositoryManagerHelper
import com.ferelin.repository.dataConverter.DataConverter
import com.ferelin.repository.dataConverter.DataConverterHelper
import com.ferelin.stockprice.dataInteractor.local.LocalInteractor
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorHelper
import dagger.Binds
import dagger.Module
import dagger.Provides
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
}