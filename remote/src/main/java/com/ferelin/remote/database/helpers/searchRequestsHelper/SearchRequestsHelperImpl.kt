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

package com.ferelin.remote.database.helpers.searchRequestsHelper

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.database.RealtimeDatabase
import com.ferelin.remote.database.RealtimeValueEventListener
import com.ferelin.remote.utils.Api
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRequestsHelperImpl @Inject constructor(
    private val mDatabaseFirebase: DatabaseReference
) : SearchRequestsHelper {

    companion object {
        private const val sSearchesHistoryRef = "search-requests"
    }

    override fun writeSearchRequestToDb(userId: String, searchRequest: String) {
        val fixedRequest = RealtimeDatabase.encrypt(searchRequest)
        mDatabaseFirebase
            .child(sSearchesHistoryRef)
            .child(userId)
            .child(fixedRequest)
            .setValue(fixedRequest)
    }

    override fun writeSearchRequestsToDb(userId: String, searchRequests: List<String>) {
        searchRequests.forEach { request -> writeSearchRequestToDb(userId, request) }
    }

    override fun readSearchRequestsFromDb(userId: String) = callbackFlow<BaseResponse<String?>> {
        mDatabaseFirebase
            .child(sSearchesHistoryRef)
            .child(userId)
            .addValueEventListener(object : RealtimeValueEventListener() {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (searchSnapshot in snapshot.children) {

                            val fixedResponse = RealtimeDatabase.decrypt(searchSnapshot?.key)
                            val response = BaseResponse<String?>(
                                responseCode = Api.RESPONSE_OK,
                                responseData = fixedResponse
                            )
                            trySend(response)
                        }
                        trySend(BaseResponse(responseCode = Api.RESPONSE_END))
                    } else trySend(BaseResponse(responseCode = Api.RESPONSE_NO_DATA))
                }
            })
        awaitClose()
    }

    override fun eraseSearchRequestFromDb(userId: String, searchRequest: String) {
        val fixedRequest = RealtimeDatabase.encrypt(searchRequest)
        mDatabaseFirebase
            .child(sSearchesHistoryRef)
            .child(userId)
            .child(fixedRequest)
            .removeValue()
    }
}