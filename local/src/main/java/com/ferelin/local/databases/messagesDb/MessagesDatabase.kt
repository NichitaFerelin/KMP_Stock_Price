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

package com.ferelin.local.databases.messagesDb

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ferelin.local.databases.DbTypesConverter
import com.ferelin.local.models.Message

@Database(entities = [Message::class], version = 1)
@TypeConverters(DbTypesConverter::class)
abstract class MessagesDatabase : RoomDatabase() {

    abstract fun messagedDao(): MessagesDao

    companion object {
        const val DB_NAME = "stockprice.messages.db"
    }
}