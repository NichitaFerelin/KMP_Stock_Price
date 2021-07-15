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

package com.ferelin.stockprice.di.bindsModules

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
import com.ferelin.remote.database.helpers.chats.ChatsHelper
import com.ferelin.remote.database.helpers.chats.ChatsHelperImpl
import com.ferelin.remote.database.helpers.favouriteCompanies.FavouriteCompaniesHelper
import com.ferelin.remote.database.helpers.favouriteCompanies.FavouriteCompaniesHelperImpl
import com.ferelin.remote.database.helpers.messages.MessagesHelper
import com.ferelin.remote.database.helpers.messages.MessagesHelperImpl
import com.ferelin.remote.database.helpers.searchRequests.SearchRequestsHelper
import com.ferelin.remote.database.helpers.searchRequests.SearchRequestsHelperImpl
import com.ferelin.remote.webSocket.connector.WebSocketConnector
import com.ferelin.remote.webSocket.connector.WebSocketConnectorImpl
import com.ferelin.repository.converter.helpers.authenticationConverter.AuthenticationConverter
import com.ferelin.repository.converter.helpers.authenticationConverter.AuthenticationConverterImpl
import dagger.Binds
import dagger.Module

@Module
abstract class RemoteBindsModule {

    @Binds
    abstract fun provideRemoteMediatorHelper(remote: RemoteMediatorImpl): RemoteMediator

    @Binds
    abstract fun provideRealtimeDatabaseManager(database: RealtimeDatabaseImpl): RealtimeDatabase

    @Binds
    abstract fun provideThrottleManager(throttleManagerImpl: ThrottleManagerImpl): ThrottleManager

    @Binds
    abstract fun provideFavouritesHelper(
        favouriteCompaniesHelperImpl: FavouriteCompaniesHelperImpl
    ): FavouriteCompaniesHelper

    @Binds
    abstract fun provideSearchRequestsHelper(
        searchRequestsHelperImpl: SearchRequestsHelperImpl
    ): SearchRequestsHelper

    @Binds
    abstract fun provideMessagesHelper(messagesHelper: MessagesHelperImpl): MessagesHelper

    @Binds
    abstract fun provideRelationsHelper(relationsHelper: ChatsHelperImpl): ChatsHelper

    @Binds
    abstract fun provideAuthenticationConverterHelper(
        authenticationConverterImpl: AuthenticationConverterImpl
    ): AuthenticationConverter

    @Binds
    abstract fun provideNetworkManagerHelper(network: ApiManagerImpl): ApiManager

    @Binds
    abstract fun provideWebSocketConnectorHelper(web: WebSocketConnectorImpl): WebSocketConnector

    @Binds
    abstract fun provideAuthenticationManagerHelper(auth: AuthenticationManagerImpl): AuthenticationManager
}
