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

package com.ferelin.local.messagesDb

import androidx.room.*
import com.ferelin.local.models.Messages

@Dao
interface MessagesDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: Messages)

    @Query("SELECT * FROM `stockprice.messages.db`")
    suspend fun getAllMessages(): List<Messages>

    @Delete
    suspend fun deleteMessage(message: Messages)

    @Query("DELETE FROM `stockprice.messages.db`")
    fun clearMessagesTable()
}