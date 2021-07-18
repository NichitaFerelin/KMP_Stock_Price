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

package com.ferelin.stockprice.di.modules

import android.content.Context
import androidx.room.Room
import com.ferelin.local.databases.chatsDb.ChatsDao
import com.ferelin.local.databases.chatsDb.ChatsDatabase
import com.ferelin.local.databases.companiesDb.CompaniesDao
import com.ferelin.local.databases.companiesDb.CompaniesDatabase
import com.ferelin.local.databases.messagesDb.MessagesDao
import com.ferelin.local.databases.messagesDb.MessagesDatabase
import com.ferelin.local.databases.searchRequestsDb.SearchRequestsDao
import com.ferelin.local.databases.searchRequestsDb.SearchRequestsDatabase
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class LocalDatabasesModule {

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
    fun provideRelationsDatabase(context: Context): ChatsDatabase {
        return Room.databaseBuilder(
            context,
            ChatsDatabase::class.java,
            ChatsDatabase.DB_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideRelationsDao(database: ChatsDatabase): ChatsDao {
        return database.chatsDao()
    }

    @Provides
    @Singleton
    fun provideSearchRequestsDatabase(context: Context): SearchRequestsDatabase {
        return Room.databaseBuilder(
            context,
            SearchRequestsDatabase::class.java,
            SearchRequestsDatabase.DB_NAME
        ).fallbackToDestructiveMigration().build()
    }

    @Provides
    fun provideSearchRequestsDao(database: SearchRequestsDatabase): SearchRequestsDao {
        return database.searchRequestsDao()
    }
}