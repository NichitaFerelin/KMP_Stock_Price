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
        const val searchRequestRef = "search-requests"

        // The key to database value is the search query itself.
        // The key cannot contain these characters.
        // They must be replaced by another available
        const val unavailableCharactersPattern = "[.$\\[\\]#/]"
        const val replacementCharacter = "%"
    }

    override suspend fun insert(
        userToken: String,
        searchRequest: String
    ): Unit = withContext(dispatchersProvider.IO) {

        val formatted = searchRequest.replace(
            Regex(unavailableCharactersPattern),
            replacementCharacter
        )

        Timber.d("insert (search request = $searchRequest, formatted = $formatted)")

        firebaseReference
            .child(searchRequestRef)
            .child(userToken)
            .child(formatted)
            .setValue(searchRequest)
    }

    override suspend fun loadAll(
        userToken: String
    ) = callbackFlow<LoadState<Set<String>>> {
        Timber.d("get all")

        firebaseReference
            .child(searchRequestRef)
            .child(userToken)
            .get()
            .addOnCompleteListener { resultSnapshot ->
                if (resultSnapshot.isSuccessful && resultSnapshot.result != null) {
                    val searchRequestsSnapshot = resultSnapshot.result!!
                    val searchRequests = mutableSetOf<String>()

                    for (searchSnapshot in searchRequestsSnapshot.children) {
                        searchRequests.add(searchSnapshot.value?.toString() ?: "")
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
            .child(searchRequestRef)
            .child(userToken)
            .removeValue()
    }

    override suspend fun erase(
        userToken: String,
        searchRequest: String
    ): Unit = withContext(dispatchersProvider.IO) {

        val formatted = searchRequest.replace(
            Regex(unavailableCharactersPattern),
            replacementCharacter
        )

        Timber.d("erase (search request = $searchRequest, formatted = $formatted)")

        firebaseReference
            .child(searchRequestRef)
            .child(userToken)
            .child(formatted)
            .removeValue()
    }
}