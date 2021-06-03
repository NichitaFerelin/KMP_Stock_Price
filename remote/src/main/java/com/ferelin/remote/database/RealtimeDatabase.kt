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

package com.ferelin.remote.database

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.utils.Api
import com.ferelin.remote.utils.offerSafe
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

/**
 * [RealtimeDatabase] is Firebase-Realtime-Database and is used to save
 * user data(such as favourite companies) in cloud.
 *
 *
 * Fore more info about methods look at [RealtimeDatabaseHelper]
 *
 * Data at cloud looks like:
 *
 *
 *      -FAVOURITE_COMPANIES
 *          -[userId1]
 *              -[companyId1]
 *              -[companyId2]
 *              -[companyId3]
 *          -[userId2]
 *              - ...
 *      -SEARCH_REQUESTS
 *          -[userId1]
 *              -[searchRequest1]
 *              -[searchRequest2]
 *              -[searchRequest3]
 */
class RealtimeDatabase @Inject constructor() : RealtimeDatabaseHelper {

    private val mDatabaseFirebase = Firebase.database.reference

    companion object {
        /*
        * Root cloud-nodes
        * */
        private const val sSearchesHistoryRef = "search-requests"
        private const val sFavouriteCompaniesRef = "favourite-companies"
    }

    override fun eraseCompanyIdFromRealtimeDb(userId: String, companyId: String) {
        mDatabaseFirebase
            .child(sFavouriteCompaniesRef)
            .child(userId)
            .child(companyId)
            .removeValue()
    }

    override fun writeCompanyIdToRealtimeDb(userId: String, companyId: String) {
        mDatabaseFirebase
            .child(sFavouriteCompaniesRef)
            .child(userId)
            .child(companyId)
            .setValue(companyId)
    }

    override fun writeCompaniesIdsToDb(userId: String, companiesId: List<String>) {
        companiesId.forEach { companyId -> writeCompanyIdToRealtimeDb(userId, companyId) }
    }

    override fun readCompaniesIdsFromDb(userId: String) = callbackFlow<BaseResponse<String?>> {
        mDatabaseFirebase
            .child(sFavouriteCompaniesRef)
            .child(userId)
            .addValueEventListener(object : RealtimeValueEventListener() {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (companySnapshot in snapshot.children) {
                            val response = BaseResponse<String?>(
                                responseCode = Api.RESPONSE_OK,
                                responseData = companySnapshot.key
                            )
                            offerSafe(response)
                        }
                        offerSafe(BaseResponse(responseCode = Api.RESPONSE_END))
                    } else offerSafe(BaseResponse(responseCode = Api.RESPONSE_NO_DATA))
                }
            })
        awaitClose()
    }

    override fun writeSearchRequestToDb(userId: String, searchRequest: String) {
        mDatabaseFirebase
            .child(sSearchesHistoryRef)
            .child(userId)
            .child(searchRequest)
            .setValue(searchRequest)
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
                            val response = BaseResponse<String?>(
                                responseCode = Api.RESPONSE_OK,
                                responseData = searchSnapshot.key
                            )
                            offer(response)
                        }
                        offer(BaseResponse(responseCode = Api.RESPONSE_END))
                    } else offer(BaseResponse(responseCode = Api.RESPONSE_NO_DATA))
                }
            })
        awaitClose()
    }

    override fun eraseSearchRequestFromDb(userId: String, searchRequest: String) {
        mDatabaseFirebase
            .child(sSearchesHistoryRef)
            .child(userId)
            .child(searchRequest)
            .removeValue()
    }
}