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

package com.ferelin.repository.helpers.remote.realtimeDatabase

import com.ferelin.repository.adaptiveModels.AdaptiveMessagesHolder
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.shared.MessageSide
import kotlinx.coroutines.flow.Flow

interface MessagesRemoteHelper {

    fun getMessagesAssociatedWithSpecifiedUserFromRealtimeDb(
        sourceUserLogin: String,
        secondSideUserLogin: String
    ): Flow<RepositoryResponse<AdaptiveMessagesHolder>>

    fun cacheNewMessageToRealtimeDb(
        sourceUserLogin: String,
        secondSideUserLogin: String,
        messageId: String,
        message: String,
        side: MessageSide
    )
}