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

import com.ferelin.local.models.Message
import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.database.helpers.messages.MessagesHelper
import com.ferelin.remote.utils.Api
import com.ferelin.repository.adaptiveModels.AdaptiveMessage
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.shared.MessageSide
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesConverterImpl @Inject constructor() : MessagesConverter {

    override fun convertMessageForLocal(adaptiveMessage: AdaptiveMessage): Message {
        return Message(
            id = adaptiveMessage.id,
            associatedUserNumber = adaptiveMessage.associatedUserNumber,
            text = adaptiveMessage.text,
            side = adaptiveMessage.side
        )
    }

    override fun convertRemoteMessageResponseForUi(
        response: BaseResponse<HashMap<String, Any>>
    ): RepositoryResponse<AdaptiveMessage> {
        return if (response.responseCode == Api.RESPONSE_OK) {
            response.responseData!!.let { responseArgs ->
                val messageId = responseArgs[MessagesHelper.MESSAGE_ID_KEY] as Int
                val messageAssociatedUserNumber = response.additionalMessage!!
                val messageText = responseArgs[MessagesHelper.MESSAGE_TEXT_KEY] as String
                val messageSide =
                    getMessageSide(responseArgs[MessagesHelper.MESSAGE_SIDE_KEY] as Char)

                RepositoryResponse.Success(
                    data = AdaptiveMessage(
                        id = messageId,
                        associatedUserNumber = messageAssociatedUserNumber,
                        side = messageSide,
                        text = messageText
                    )
                )
            }
        } else RepositoryResponse.Failed()
    }

    override fun convertLocalMessagesResponseForUi(
        messages: List<Message>?
    ): RepositoryResponse<List<AdaptiveMessage>> {
        return if (messages != null) {
            RepositoryResponse.Success(
                data = messages.map {
                    AdaptiveMessage(
                        id = it.id,
                        associatedUserNumber = it.associatedUserNumber,
                        side = it.side,
                        text = it.text
                    )
                }
            )
        } else RepositoryResponse.Failed()
    }

    private fun getMessageSide(messageSide: Char): MessageSide {
        return if (messageSide == MessageSide.Source.key) {
            MessageSide.Source
        } else MessageSide.Associated
    }
}