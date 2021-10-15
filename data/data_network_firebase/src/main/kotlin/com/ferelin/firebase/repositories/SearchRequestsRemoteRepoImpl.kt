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

package com.ferelin.firebase.repositories

import com.ferelin.domain.entities.SearchRequest
import com.ferelin.domain.repositories.searchRequests.SearchRequestsRemoteRepo
import com.ferelin.shared.DispatchersProvider
import com.ferelin.shared.LoadState
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class SearchRequestsRemoteRepoImpl @Inject constructor(
    private val mFirebaseReference: DatabaseReference,
    private val mDispatchersProvider: DispatchersProvider
) : SearchRequestsRemoteRepo {

    private companion object {
        const val sSearchesHistoryRef = "search-requests"
    }

    override suspend fun cacheSearchRequest(
        userToken: String,
        searchRequest: SearchRequest
    ): Unit = withContext(mDispatchersProvider.IO) {
        Timber.d("cache search request ($searchRequest)")

        mFirebaseReference
            .child(sSearchesHistoryRef)
            .child(userToken)
            .child(searchRequest.id.toString())
            .setValue(searchRequest.request)
    }

    override suspend fun eraseSearchRequest(
        userToken: String,
        searchRequest: SearchRequest
    ): Unit = withContext(mDispatchersProvider.IO) {
        Timber.d("erase search request ($searchRequest)")

        mFirebaseReference
            .child(sSearchesHistoryRef)
            .child(userToken)
            .child(searchRequest.id.toString())
            .removeValue()
    }

    override suspend fun loadSearchRequests(
        userToken: String
    ): LoadState<List<SearchRequest>> = withContext(mDispatchersProvider.IO) {
        Timber.d("load search requests (userToken = $userToken")

        val resultSnapshot = mFirebaseReference
            .child(sSearchesHistoryRef)
            .child(userToken)
            .get()

        return@withContext if (resultSnapshot.isSuccessful && resultSnapshot.result != null) {
            val searchRequestsSnapshot = resultSnapshot.result!!
            val searchRequests = mutableListOf<SearchRequest>()

            for (searchSnapshot in searchRequestsSnapshot.children) {
                val requestId = searchSnapshot.key?.toInt() ?: 0
                val request = searchSnapshot.value?.toString() ?: ""
                searchRequests.add(
                    SearchRequest(requestId, request)
                )
            }

            LoadState.Prepared(searchRequests)
        } else {
            LoadState.Error()
        }
    }

    override suspend fun clearSearchRequests(userToken: String) {
        mFirebaseReference
            .child(sSearchesHistoryRef)
            .child(userToken)
            .removeValue()
    }
}