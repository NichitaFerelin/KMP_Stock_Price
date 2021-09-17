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

package com.ferelin.firebase.database.searchRequests

import com.google.firebase.database.DatabaseReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SearchRequestsRefImpl @Inject constructor(
    private val mFirebaseReference: DatabaseReference
) : SearchRequestsRef {

    private companion object {
        const val sSearchesHistoryRef = "search-requests"
    }

    override fun cacheSearchRequest(userToken: String, searchRequest: String) {
        mFirebaseReference
            .child(sSearchesHistoryRef)
            .child(userToken)
            .child(searchRequest)
            .push()
    }

    override fun eraseSearchRequest(userToken: String, searchRequest: String) {
        mFirebaseReference
            .child(sSearchesHistoryRef)
            .child(userToken)
            .child(searchRequest)
            .removeValue()
    }

    override fun getSearchRequests(userToken: String): List<String> {
        val resultSnapshot = mFirebaseReference
            .child(sSearchesHistoryRef)
            .child(userToken)
            .get()

        return if (resultSnapshot.isSuccessful && resultSnapshot.result != null) {
            val searchRequestsSnapshot = resultSnapshot.result!!
            val searchRequests = mutableListOf<String>()

            for (searchSnapshot in searchRequestsSnapshot.children) {
                val request = searchSnapshot.value?.toString() ?: ""
                searchRequests.add(request)
            }

            searchRequests
        } else {
            emptyList()
        }
    }
}