package com.ferelin.local.database

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

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ferelin.local.database.CompaniesDatabase.Companion.DB_NAME
import com.ferelin.local.models.Company
import com.ferelin.shared.SingletonHolder

@Database(entities = [Company::class], version = 1)
@TypeConverters(CompaniesConverter::class)
abstract class CompaniesDatabase : RoomDatabase() {

    abstract fun companiesDao(): CompaniesDao

    companion object : SingletonHolder<CompaniesDatabase, Context>({
        Room.databaseBuilder(
            it.applicationContext,
            CompaniesDatabase::class.java,
            DB_NAME
        ).fallbackToDestructiveMigration().build()
    }) {
        const val DB_NAME = "stockprice.companies.db"
    }
}