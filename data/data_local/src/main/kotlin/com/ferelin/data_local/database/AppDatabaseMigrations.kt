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

package com.ferelin.data_local.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

class AppDatabaseMigrations {

    companion object {

        val migration_1_to_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("DROP TABLE search_requests")
            }
        }
        val migration_2_to_3 = object : Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {

                database.execSQL(
                    "CREATE TABLE IF NOT EXISTS `crypto` (" +
                            "`name` TEXT NOT NULL, " +
                            "`symbol` TEXT NOT NULL, " +
                            "`crypto_id` INTEGER NOT NULL DEFAULT 0, " +
                            "`logo_url` TEXT NOT NULL, " +
                            "PRIMARY KEY(`crypto_id`)" +
                            ")"
                )

                database.execSQL(
                    "CREATE TABLE `crypto_prices` (" +
                            "`relation_crypto_id` INTEGER NOT NULL DEFAULT 0, " +
                            "`price` DOUBLE NOT NULL, " +
                            "`price_timestamp` TEXT NOT NULL, " +
                            "`high_price` DOUBLE NOT NULl, " +
                            "`high_price_timestamp` TEXT NOT NULL, " +
                            "`price_change` DOUBLE NOT NULL, " +
                            "`price_change_percents` DOUBLE NOT NULL, " +
                            "PRIMARY KEY(`relation_crypto_id`)" +
                            ")"
                )
            }
        }
    }
}