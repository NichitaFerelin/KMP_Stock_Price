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

package com.ferelin.remote.database.helpers.user

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.database.RealtimeDatabase
import com.ferelin.remote.database.RealtimeValueEventListener
import com.ferelin.remote.utils.Api
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UsersHelperImpl @Inject constructor(
    private val mDatabaseFirebase: DatabaseReference
) : UsersHelper {

    companion object {
        private const val sUsersRef = "users"
    }

    override suspend fun tryToRegister(userId: String, login: String) =
        callbackFlow<BaseResponse<Boolean>> {
            if (!RealtimeDatabase.isTextAvailableForFirebase(login)) {
                trySend(BaseResponse(responseCode = Api.RESPONSE_BAD_LOGIN))
            }

            findUserById(userId).firstOrNull()?.let { isRegistered ->
                if (isRegistered) {
                    trySend(BaseResponse(responseCode = Api.RESPONSE_LOGIN_EXISTS))
                } else {
                    cacheNewUser(userId, login)
                    trySend(BaseResponse(responseCode = Api.RESPONSE_OK, responseData = true))
                }
            } ?: trySend(BaseResponse(responseCode = Api.RESPONSE_UNDEFINED))

            awaitClose()
        }

    override fun findUserById(userId: String) : Flow<Boolean> = callbackFlow {
        mDatabaseFirebase
            .child(sUsersRef)
            .child(userId)
            .addValueEventListener(object : RealtimeValueEventListener() {
                override fun onDataChange(snapshot: DataSnapshot) {
                    trySend(snapshot.exists())
                }
            })
        awaitClose()
    }

    override fun findUserByLogin(login: String) : Flow<Boolean> = callbackFlow {
        mDatabaseFirebase
            .child(sUsersRef)
            .addValueEventListener(object : RealtimeValueEventListener() {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (userSnapshot in snapshot.children) {
                            if (userSnapshot.value == login) {
                                trySend(true)
                                cancel()
                            }
                        }
                    }
                    trySend(false)
                }
            })
        awaitClose()
    }


    private fun cacheNewUser(userId: String, login: String) {
        mDatabaseFirebase
            .child(sUsersRef)
            .child(userId)
            .setValue(login)
    }
}