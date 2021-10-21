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

package com.ferelin.data_network_firebase.repositories

import com.ferelin.domain.entities.SearchRequest
import com.ferelin.domain.repositories.searchRequests.SearchRequestsRemoteRepo
import com.ferelin.shared.DispatchersProvider
import com.ferelin.shared.LoadState
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class SearchRequestsRemoteRepoImpl @Inject constructor(
    private val firebaseReference: DatabaseReference,
    private val dispatchersProvider: DispatchersProvider
) : SearchRequestsRemoteRepo {

    private companion object {
        const val SEARCH_REQUEST_REF = "search-requests"
    }

    override suspend fun insert(
        userToken: String,
        searchRequest: SearchRequest
    ): Unit = withContext(dispatchersProvider.IO) {
        Timber.d("insert (search request = $searchRequest)")

        firebaseReference
            .child(SEARCH_REQUEST_REF)
            .child(userToken)
            .child(searchRequest.id.toString())
            .setValue(searchRequest.request)
    }

    override suspend fun loadAll(
        userToken: String
    ) = callbackFlow<LoadState<List<SearchRequest>>> {
        Timber.d("get all")

        firebaseReference
            .child(SEARCH_REQUEST_REF)
            .child(userToken)
            .get()
            .addOnCompleteListener { resultSnapshot ->
                if (resultSnapshot.isSuccessful && resultSnapshot.result != null) {
                    val searchRequestsSnapshot = resultSnapshot.result!!
                    val searchRequests = mutableListOf<SearchRequest>()

                    for (searchSnapshot in searchRequestsSnapshot.children) {
                        val requestId = searchSnapshot.key?.toInt() ?: 0
                        val request = searchSnapshot.value?.toString() ?: ""
                        val searchRequest = SearchRequest(requestId, request)

                        searchRequests.add(searchRequest)
                    }

                    trySend(LoadState.Prepared(searchRequests))
                } else {
                    trySend(LoadState.Error())
                }
            }
        awaitClose()
    }.flowOn(dispatchersProvider.IO)

    override suspend fun eraseAll(userToken: String) {
        Timber.d("erase all")

        firebaseReference
            .child(SEARCH_REQUEST_REF)
            .child(userToken)
            .removeValue()
    }

    override suspend fun erase(
        userToken: String,
        searchRequest: SearchRequest
    ): Unit = withContext(dispatchersProvider.IO) {
        Timber.d("erase (search request = $searchRequest)")

        firebaseReference
            .child(SEARCH_REQUEST_REF)
            .child(userToken)
            .child(searchRequest.id.toString())
            .removeValue()
    }
}