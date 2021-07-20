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

package com.ferelin.remote.database.helpers.chats

import com.ferelin.remote.base.BaseResponse
import kotlinx.coroutines.flow.Flow

/**
 * [ChatsHelper] provides methods for interacting with database chats
 * */
interface ChatsHelper {

    fun cacheChat(chatId: String, currentUserNumber: String, associatedUserNumber: String)

    /**
     * @return chats by one associated with [userNumber]
     * */
    fun getChatsByUserNumber(userNumber: String): Flow<BaseResponse<String>>
}