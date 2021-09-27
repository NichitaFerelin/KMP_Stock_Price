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

import com.ferelin.domain.repositories.searchRequests.SearchRequestsLoadState
import com.ferelin.domain.repositories.searchRequests.SearchRequestsRemoteRepo
import com.ferelin.shared.CoroutineContextProvider
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.withContext
import javax.inject.Inject

class SearchRequestsRemoteRepoImpl @Inject constructor(
    private val mFirebaseReference: DatabaseReference,
    private val mCoroutineContextProvider: CoroutineContextProvider
) : SearchRequestsRemoteRepo {

    private companion object {
        const val sSearchesHistoryRef = "search-requests"
    }

    override suspend fun cacheSearchRequest(
        userToken: String,
        searchRequest: String
    ): Unit = withContext(mCoroutineContextProvider.IO) {

        mFirebaseReference
            .child(sSearchesHistoryRef)
            .child(userToken)
            .child(searchRequest)
            .push()
    }

    override suspend fun eraseSearchRequest(
        userToken: String,
        searchRequest: String
    ): Unit = withContext(mCoroutineContextProvider.IO) {

        mFirebaseReference
            .child(sSearchesHistoryRef)
            .child(userToken)
            .child(searchRequest)
            .removeValue()
    }

    override suspend fun loadSearchRequests(
        userToken: String
    ): SearchRequestsLoadState = withContext(mCoroutineContextProvider.IO) {

        val resultSnapshot = mFirebaseReference
            .child(sSearchesHistoryRef)
            .child(userToken)
            .get()

        return@withContext if (resultSnapshot.isSuccessful && resultSnapshot.result != null) {
            val searchRequestsSnapshot = resultSnapshot.result!!
            val searchRequests = mutableListOf<String>()

            for (searchSnapshot in searchRequestsSnapshot.children) {
                val request = searchSnapshot.value?.toString() ?: ""
                searchRequests.add(request)
            }

            SearchRequestsLoadState.Loaded(searchRequests)
        } else {
            SearchRequestsLoadState.Error
        }
    }
}