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

import com.ferelin.remote.RESPONSE_NO_DATA
import com.ferelin.remote.RESPONSE_OK
import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.database.utils.ChildChangedListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatsHelperImpl @Inject constructor(
    private val mFirebaseReference: DatabaseReference
) : ChatsHelper {

    private companion object {
        const val sChatsReference = "chats"
    }

    override fun cacheChat(
        chatId: String,
        currentUserNumber: String,
        associatedUserNumber: String
    ) {
        mFirebaseReference
            .child(sChatsReference)
            .child(currentUserNumber)
            .child(chatId)
            .setValue(associatedUserNumber)
    }

    override fun getChatsByUserNumber(userNumber: String): Flow<BaseResponse<String>> =
        callbackFlow {
            mFirebaseReference
                .child(sChatsReference)
                .child(userNumber)
                .addChildEventListener(object : ChildChangedListener() {
                    override fun onChildAdded(snapshot: DataSnapshot, previousChildName: String?) {
                        if (snapshot.hasChildren()) {
                            for (chatSnapshot in snapshot.children) {
                                trySend(
                                    element = createResponseBySnapshot(chatSnapshot)
                                )
                            }
                        } else {
                            trySend(
                                element = createResponseBySnapshot(snapshot)
                            )
                        }
                    }
                })
            awaitClose()
        }

    private fun createResponseBySnapshot(chatSnapshot: DataSnapshot): BaseResponse<String> {
        return if (chatSnapshot.exists()) {
            val associatedUserNumber = chatSnapshot.value?.toString() ?: ""
            val chatId = chatSnapshot.key ?: "0"
            BaseResponse(
                responseCode = RESPONSE_OK,
                additionalMessage = chatId,
                responseData = associatedUserNumber
            )
        } else BaseResponse(responseCode = RESPONSE_NO_DATA)
    }
}