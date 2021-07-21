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

import com.ferelin.repository.Repository
import com.ferelin.repository.RepositoryImpl
import com.ferelin.repository.converter.ConverterMediator
import com.ferelin.repository.converter.ConverterMediatorImpl
import com.ferelin.repository.converter.helpers.apiConverter.ApiConverter
import com.ferelin.repository.converter.helpers.apiConverter.ApiConverterImpl
import com.ferelin.repository.converter.helpers.chatsConverter.ChatsConverter
import com.ferelin.repository.converter.helpers.chatsConverter.ChatsConverterImpl
import com.ferelin.repository.converter.helpers.companiesConverter.CompaniesConverter
import com.ferelin.repository.converter.helpers.companiesConverter.CompaniesConverterImpl
import com.ferelin.repository.converter.helpers.messagesConverter.MessagesConverter
import com.ferelin.repository.converter.helpers.messagesConverter.MessagesConverterImpl
import com.ferelin.repository.converter.helpers.realtimeConverter.RealtimeDatabaseConverter
import com.ferelin.repository.converter.helpers.realtimeConverter.RealtimeDatabaseConverterImpl
import com.ferelin.repository.converter.helpers.searchRequestsConverter.SearchRequestsConverter
import com.ferelin.repository.converter.helpers.searchRequestsConverter.SearchRequestsConverterImpl
import com.ferelin.repository.converter.helpers.webSocketConverter.WebSocketConverter
import com.ferelin.repository.converter.helpers.webSocketConverter.WebSocketConverterImpl
import dagger.Binds
import dagger.Module

@Module
abstract class RepositoryBindsModule {

    @Binds
    abstract fun provideRepositoryManagerHelper(repositoryImpl: RepositoryImpl): Repository

    @Binds
    abstract fun provideResponseMediator(responseMediator: ConverterMediatorImpl): ConverterMediator

    @Binds
    abstract fun provideApiConverter(apiConverterImpl: ApiConverterImpl): ApiConverter

    @Binds
    abstract fun provideCompaniesConverter(
        companiesConverterImpl: CompaniesConverterImpl
    ): CompaniesConverter

    @Binds
    abstract fun provideMessagesConverter(
        messagesConverterImpl: MessagesConverterImpl
    ): MessagesConverter

    @Binds
    abstract fun provideRealtimeDatabaseConverter(
        realtimeDatabaseConverterImpl: RealtimeDatabaseConverterImpl
    ): RealtimeDatabaseConverter

    @Binds
    abstract fun provideSearchRequestsConverter(
        searchRequestsConverterImpl: SearchRequestsConverterImpl
    ): SearchRequestsConverter

    @Binds
    abstract fun provideWebSocketConverter(
        webSocketConverterImpl: WebSocketConverterImpl
    ): WebSocketConverter

    @Binds
    abstract fun provideChatsConverter(
        relationsConverterImpl: ChatsConverterImpl
    ): ChatsConverter
}