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

package com.ferelin.remote.database.helpers.favouriteCompanies

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.database.utils.ValueEventListener
import com.ferelin.remote.utils.Api
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavouriteCompaniesHelperImpl @Inject constructor(
    private val mDatabaseFirebase: DatabaseReference
) : FavouriteCompaniesHelper {

    companion object {
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
            .addValueEventListener(object : ValueEventListener() {
                // TODO .get()
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        for (companySnapshot in snapshot.children) {
                            val response = BaseResponse<String?>(
                                responseCode = Api.RESPONSE_OK,
                                responseData = companySnapshot.key
                            )
                            trySend(response)
                        }
                        trySend(BaseResponse(responseCode = Api.RESPONSE_END))
                    } else trySend(BaseResponse(responseCode = Api.RESPONSE_NO_DATA))
                }
            })
        awaitClose()
    }
}