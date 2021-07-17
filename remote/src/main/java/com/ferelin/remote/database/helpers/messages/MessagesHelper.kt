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

package com.ferelin.remote.database.helpers.messages

import com.ferelin.remote.base.BaseResponse
import kotlinx.coroutines.flow.Flow

interface MessagesHelper {

    companion object {
        const val MESSAGE_ID_KEY = "message_id_key"
        const val MESSAGE_TEXT_KEY = "message_text_key"
        const val MESSAGE_SIDE_KEY = "message_side_key"
    }

    fun getMessagesForChat(
        currentUserNumber: String,
        associatedUserNumber: String
    ): Flow<BaseResponse<HashMap<String, Any>>>

    fun cacheMessage(
        id: String,
        currentUserNumber: String,
        associatedUserNumber: String,
        messageText: String,
        messageSideKey: Char
    )
}