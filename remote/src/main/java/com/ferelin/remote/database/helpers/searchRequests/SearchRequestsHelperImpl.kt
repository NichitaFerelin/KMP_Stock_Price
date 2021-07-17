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

package com.ferelin.remote.database.helpers.searchRequests

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.utils.Api
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

    override fun writeSearchRequestToDb(
        userId: String,
        searchRequestId: String,
        searchRequest: String
    ) {
        mDatabaseFirebase
            .child(sSearchesHistoryRef)
            .child(userId)
            .child(searchRequestId)
            .setValue(searchRequest)
    }

    override fun eraseSearchRequestFromDb(userId: String, searchRequestId: String) {
        mDatabaseFirebase
            .child(sSearchesHistoryRef)
            .child(userId)
            .child(searchRequestId)
            .removeValue()
    }

    override fun readSearchRequestsFromDb(
        userId: String
    ) = callbackFlow<BaseResponse<List<String>>> {
        mDatabaseFirebase
            .child(sSearchesHistoryRef)
            .child(userId)
            .get()
            .addOnSuccessListener { searchRequestsSnapshot ->
                val searchRequests = mutableListOf<String>()
                for (iteratorSearchSnapshot in searchRequestsSnapshot.children) {
                    searchRequests.add(iteratorSearchSnapshot.value?.toString() ?: "")
                }
                trySend(
                    BaseResponse(
                        responseCode = Api.RESPONSE_OK,
                        responseData = searchRequests
                    )
                )
            }.addOnFailureListener { trySend(BaseResponse(responseCode = Api.RESPONSE_UNDEFINED)) }

        awaitClose()
    }
}