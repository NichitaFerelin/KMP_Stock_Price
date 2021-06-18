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
import com.ferelin.local.models.MessagesHolder
import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.database.helpers.messages.MessagesHelperImpl
import com.ferelin.remote.utils.Api
import com.ferelin.repository.adaptiveModels.AdaptiveMessage
import com.ferelin.repository.adaptiveModels.AdaptiveMessagesHolder
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.shared.MessageSide
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesConverterImpl @Inject constructor() : MessagesConverter {

    override fun convertMessageForLocal(messagesHolder: AdaptiveMessagesHolder): MessagesHolder {
        return MessagesHolder(
            id = messagesHolder.id,
            secondSideLogin = messagesHolder.secondSideLogin,
            messages = messagesHolder.messages.map { message ->
                Message(
                    id = message.id,
                    side = message.side,
                    text = message.text
                )
            }
        )
    }

    override fun convertRemoteMessagesResponseForUi(
        response: BaseResponse<List<HashMap<String, String>>>
    ): RepositoryResponse<AdaptiveMessagesHolder> {
        return if (response.responseCode == Api.RESPONSE_OK) {
            RepositoryResponse.Success(
                data = AdaptiveMessagesHolder(
                    secondSideLogin = response.additionalMessage!!,
                    messages = response.responseData!!.map { map ->
                        val messageId = map[MessagesHelperImpl.MESSAGE_ID_KEY]!!.toInt()
                        val side = map[MessagesHelperImpl.MESSAGE_SIDE_KEY]!![0]
                        val messageSide = if (side == MessageSide.Source.key) {
                            MessageSide.Source
                        } else MessageSide.Associated
                        val messageText = map[MessagesHelperImpl.MESSAGE_TEXT_KEY].toString()

                        AdaptiveMessage(
                            id = messageId,
                            side = messageSide,
                            text = messageText
                        )
                    }.toMutableList()
                )
            )
        } else RepositoryResponse.Failed()
    }

    override fun convertLocalMessagesResponseForUi(
        holder: MessagesHolder
    ): RepositoryResponse<AdaptiveMessagesHolder> {
        return RepositoryResponse.Success(
            data = AdaptiveMessagesHolder(
                id = holder.id,
                secondSideLogin = holder.secondSideLogin,
                messages = holder.messages.map { localMessage ->
                    AdaptiveMessage(
                        id = localMessage.id,
                        side = localMessage.side,
                        text = localMessage.text
                    )
                }.toMutableList()
            )
        )
    }
}