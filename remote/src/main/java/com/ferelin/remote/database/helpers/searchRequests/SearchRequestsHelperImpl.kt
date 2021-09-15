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

import com.ferelin.remote.RESPONSE_OK
import com.ferelin.remote.RESPONSE_UNDEFINED
import com.ferelin.remote.base.BaseResponse
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRequestsHelperImpl @Inject constructor(
    private val mFirebaseReference: DatabaseReference
) : SearchRequestsHelper {

    private companion object {
        const val sSearchesHistoryRef = "search-requests"
    }

    override fun cacheSearchRequestToDb(
        userToken: String,
        searchRequestId: String,
        searchRequest: String
    ) {
        mFirebaseReference
            .child(sSearchesHistoryRef)
            .child(userToken)
            .child(searchRequestId)
            .setValue(searchRequest)
    }

    override fun eraseSearchRequestFromDb(userToken: String, searchRequestId: String) {
        mFirebaseReference
            .child(sSearchesHistoryRef)
            .child(userToken)
            .child(searchRequestId)
            .removeValue()
    }

    override fun getSearchRequestsFromDb(
        userToken: String
    ) = callbackFlow<BaseResponse<HashMap<Int, String>>> {
        mFirebaseReference
            .child(sSearchesHistoryRef)
            .child(userToken)
            .get()
            .addOnSuccessListener { searchRequestsSnapshot ->
                val searchRequests = hashMapOf<Int, String>()
                for (iteratorSearchSnapshot in searchRequestsSnapshot.children) {
                    val searchRequestText = iteratorSearchSnapshot.value?.toString() ?: ""
                    val searchRequestId = iteratorSearchSnapshot.key?.toInt() ?: 0
                    searchRequests[searchRequestId] = searchRequestText
                }
                trySend(
                    BaseResponse(
                        responseCode = RESPONSE_OK,
                        responseData = searchRequests
                    )
                )
            }.addOnFailureListener { trySend(BaseResponse(responseCode = RESPONSE_UNDEFINED)) }

        awaitClose()
    }
}