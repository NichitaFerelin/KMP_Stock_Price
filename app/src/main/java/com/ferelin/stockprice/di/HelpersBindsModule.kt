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

import com.ferelin.local.LocalManager
import com.ferelin.local.LocalManagerImpl
import com.ferelin.local.database.CompaniesManager
import com.ferelin.local.database.CompaniesManagerImpl
import com.ferelin.local.json.JsonManager
import com.ferelin.local.json.JsonManagerImpl
import com.ferelin.local.preferences.StorePreferences
import com.ferelin.local.preferences.StorePreferencesImpl
import com.ferelin.remote.RemoteMediator
import com.ferelin.remote.RemoteMediatorImpl
import com.ferelin.remote.api.ApiManager
import com.ferelin.remote.api.ApiManagerImpl
import com.ferelin.remote.api.throttleManager.ThrottleManager
import com.ferelin.remote.api.throttleManager.ThrottleManagerImpl
import com.ferelin.remote.auth.AuthenticationManager
import com.ferelin.remote.auth.AuthenticationManagerImpl
import com.ferelin.remote.database.RealtimeDatabase
import com.ferelin.remote.database.RealtimeDatabaseImpl
import com.ferelin.remote.webSocket.connector.WebSocketConnector
import com.ferelin.remote.webSocket.connector.WebSocketConnectorImpl
import com.ferelin.repository.Repository
import com.ferelin.repository.RepositoryImpl
import com.ferelin.repository.converter.ResponseConverter
import com.ferelin.repository.converter.ResponseConverterImpl
import com.ferelin.stockprice.dataInteractor.local.LocalInteractor
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorImpl
import dagger.Binds
import dagger.Module

@Module
abstract class HelpersBindsModule {

    @Binds
    abstract fun provideRepositoryManagerHelper(repositoryImpl: RepositoryImpl): Repository

    @Binds
    abstract fun provideLocalInteractorHelper(localInteractor: LocalInteractorImpl): LocalInteractor

    @Binds
    abstract fun provideRemoteMediatorHelper(remote: RemoteMediatorImpl): RemoteMediator

    @Binds
    abstract fun provideNetworkManagerHelper(network: ApiManagerImpl): ApiManager

    @Binds
    abstract fun provideWebSocketConnectorHelper(web: WebSocketConnectorImpl): WebSocketConnector

    @Binds
    abstract fun provideLocalManagerHelper(local: LocalManagerImpl): LocalManager

    @Binds
    abstract fun provideJsonManagerHelper(json: JsonManagerImpl): JsonManager

    @Binds
    abstract fun provideCompaniesManagerHelper(companies: CompaniesManagerImpl): CompaniesManager

    @Binds
    abstract fun provideStorePreferencesHelper(store: StorePreferencesImpl): StorePreferences

    @Binds
    abstract fun provideDataConverterHelper(responseConverter: ResponseConverterImpl): ResponseConverter

    @Binds
    abstract fun provideAuthenticationManagerHelper(auth: AuthenticationManagerImpl): AuthenticationManager

    @Binds
    abstract fun provideRealtimeDatabaseManager(database: RealtimeDatabaseImpl): RealtimeDatabase

    @Binds
    abstract fun provideThrottleManager(throttleManagerImpl: ThrottleManagerImpl): ThrottleManager
}
