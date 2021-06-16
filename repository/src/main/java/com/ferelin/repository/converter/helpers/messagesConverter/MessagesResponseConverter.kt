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

package com.ferelin.repository.converter.helpers.messagesConverter

import com.ferelin.local.models.MessagesHolder
import com.ferelin.remote.base.BaseResponse
import com.ferelin.repository.adaptiveModels.AdaptiveMessagesHolder
import com.ferelin.repository.utils.RepositoryResponse

interface MessagesResponseConverter {

    fun convertMessageForLocal(messagesHolder: AdaptiveMessagesHolder): MessagesHolder

    fun convertRemoteMessagesResponseForUi(
        response: BaseResponse<List<HashMap<String, String>>>
    ): RepositoryResponse<AdaptiveMessagesHolder>

    fun convertLocalMessagesResponseForUi(
        items: List<MessagesHolder>
    ): RepositoryResponse<List<AdaptiveMessagesHolder>>
}