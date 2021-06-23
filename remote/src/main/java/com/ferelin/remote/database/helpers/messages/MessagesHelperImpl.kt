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
import com.ferelin.remote.database.RealtimeDatabase
import com.ferelin.remote.database.RealtimeValueEventListener
import com.ferelin.remote.utils.Api
import com.ferelin.shared.MessageSide
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesHelperImpl @Inject constructor(
    private val mDatabaseFirebase: DatabaseReference
) : MessagesHelper {

    companion object {
        private const val sMessagesRef = "messages"

        const val MESSAGE_ID_KEY = "message_id_key"
        const val MESSAGE_TEXT_KEY = "message_text_key"
        const val MESSAGE_SIDE_KEY = "message_side_key"
    }

    override fun getMessagesAssociatedWithSpecifiedUser(
        sourceUserLogin: String,
        secondSideUserLogin: String
    ) = callbackFlow<BaseResponse<List<HashMap<String, String>>>> {
        val encryptedSecondSideLogin = RealtimeDatabase.encrypt(secondSideUserLogin)
        mDatabaseFirebase
            .child(sMessagesRef)
            .child(sourceUserLogin)
            .child(encryptedSecondSideLogin)
            .addValueEventListener(object : RealtimeValueEventListener() {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val messages = mutableListOf<HashMap<String, String>>()
                        for (messageSnapshot in snapshot.children) {
                            messageSnapshot.key?.let { messageSideWithId ->
                                val messageSide = messageSideWithId[0].toString()
                                val messageId = messageSideWithId.filter { it.isDigit() }
                                val decryptedMessage =
                                    RealtimeDatabase.decrypt(messageSnapshot.value.toString())!!

                                messages.add(
                                    hashMapOf(
                                        MESSAGE_ID_KEY to messageId,
                                        MESSAGE_SIDE_KEY to messageSide,
                                        MESSAGE_TEXT_KEY to decryptedMessage
                                    )
                                )
                            }
                        }
                        trySend(
                            BaseResponse(
                                responseCode = Api.RESPONSE_OK,
                                additionalMessage = secondSideUserLogin,
                                responseData = messages
                            )
                        )
                    } else trySend(BaseResponse(Api.RESPONSE_NO_DATA))
                }
            })
        awaitClose()
    }

    override fun addNewMessage(
        sourceUserLogin: String,
        secondSideUserLogin: String,
        messageId: String,
        message: String,
        side: MessageSide
    ) {
        val key = when (side) {
            is MessageSide.Source -> MessageSide.Source.key
            is MessageSide.Associated -> MessageSide.Associated.key
        }
        val messageKey = key + messageId
        val encryptedMessage = RealtimeDatabase.encrypt(message)
        mDatabaseFirebase
            .child(sMessagesRef)
            .child(sourceUserLogin)
            .child(secondSideUserLogin)
            .child(messageKey)
            .setValue(encryptedMessage)
    }
}