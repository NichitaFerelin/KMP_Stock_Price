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

package com.ferelin.repository.converter.helpers.chatsConverter

import com.ferelin.local.models.Chat
import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.utils.Api
import com.ferelin.repository.adaptiveModels.AdaptiveChat
import com.ferelin.repository.utils.RepositoryResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatsConverterImpl @Inject constructor() : ChatsConverter {

    override fun convertAdaptiveChatForLocal(adaptiveChat: AdaptiveChat): Chat {
        return Chat(
            id = adaptiveChat.id,
            associatedUserNumber = adaptiveChat.associatedUserNumber,
            previewText = adaptiveChat.previewText
        )
    }

    override fun convertLocalChatsForUi(chats: List<Chat>?): RepositoryResponse<List<AdaptiveChat>> {
        return if (chats != null) {
            RepositoryResponse.Success(
                data = chats.map { chat ->
                    AdaptiveChat(
                        id = chat.id,
                        associatedUserNumber = chat.associatedUserNumber,
                        previewText = chat.previewText
                    )
                }
            )
        } else RepositoryResponse.Failed()
    }

    override fun convertRemoteChatResponseForUi(
        response: BaseResponse<String>
    ): RepositoryResponse<AdaptiveChat> {
        return if (response.responseCode == Api.RESPONSE_OK) {
            RepositoryResponse.Success(
                data = AdaptiveChat(
                    id = response.additionalMessage!!.toInt(),
                    associatedUserNumber = response.responseData!!
                )
            )
        } else RepositoryResponse.Failed()
    }
}