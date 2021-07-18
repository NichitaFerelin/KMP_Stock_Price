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
import com.ferelin.remote.database.utils.ChildChangedListener
import com.ferelin.remote.utils.Api
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class MessagesHelperImpl @Inject constructor(
    private val mDatabaseReference: DatabaseReference
) : MessagesHelper {

    private companion object {
        const val sMessagesReference = "messages"
    }

    override fun getMessagesForChat(
        currentUserNumber: String,
        associatedUserNumber: String
    ) = callbackFlow<BaseResponse<HashMap<String, Any>>> {
        mDatabaseReference
            .child(sMessagesReference)
            .child(currentUserNumber)
            .child(associatedUserNumber)
            .get()
            .addOnSuccessListener { dataSnapshot ->
                for (messageSnapshot in dataSnapshot.children) {
                    trySend(
                        element = createResponseBySnapshot(associatedUserNumber, messageSnapshot)
                    )
                }
            }
            .addOnFailureListener { trySend(BaseResponse(responseCode = Api.RESPONSE_NO_DATA)) }

        mDatabaseReference
            .child(sMessagesReference)
            .child(currentUserNumber)
            .child(associatedUserNumber)
            .addChildEventListener(object : ChildChangedListener() {
                override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                    trySend(
                        element = createResponseBySnapshot(associatedUserNumber, snapshot)
                    )
                }
            })

        awaitClose()
    }

    override fun cacheMessage(
        id: String,
        currentUserNumber: String,
        associatedUserNumber: String,
        messageText: String,
        messageSideKey: Char
    ) {
        val messageWithSideKey = "$messageSideKey $messageText"

        mDatabaseReference
            .child(sMessagesReference)
            .child(currentUserNumber)
            .child(associatedUserNumber)
            .child(id)
            .setValue(messageWithSideKey)
    }


    private fun createResponseBySnapshot(
        associatedUserNumber: String,
        messageSnapshot: DataSnapshot
    ): BaseResponse<HashMap<String, Any>> {
        return if (messageSnapshot.exists()) {
            messageSnapshot.value?.let { snapshotValue ->
                val valueStr = snapshotValue.toString()
                val messageSide = valueStr[0]
                val mainMessage = valueStr.substring(2)
                val messageId = messageSnapshot.key?.toInt() ?: 0

                BaseResponse(
                    responseCode = Api.RESPONSE_OK,
                    responseData = hashMapOf(
                        MessagesHelper.MESSAGE_ID_KEY to messageId,
                        MessagesHelper.MESSAGE_SIDE_KEY to messageSide,
                        MessagesHelper.MESSAGE_TEXT_KEY to mainMessage
                    ),
                    additionalMessage = associatedUserNumber
                )
            } ?: BaseResponse.failed()
        } else BaseResponse.failed()
    }
}