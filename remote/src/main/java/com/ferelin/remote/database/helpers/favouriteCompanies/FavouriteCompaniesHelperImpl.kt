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
import com.ferelin.remote.utils.Api
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavouriteCompaniesHelperImpl @Inject constructor(
    private val mDatabaseReference: DatabaseReference
) : FavouriteCompaniesHelper {

    private companion object {
        const val sFavouriteCompaniesRef = "favourite-companies"
    }

    override fun eraseCompanyIdFromRealtimeDb(userToken: String, companyId: String) {
        mDatabaseReference
            .child(sFavouriteCompaniesRef)
            .child(userToken)
            .child(companyId)
            .removeValue()
    }

    override fun cacheCompanyIdToRealtimeDb(userToken: String, companyId: String) {
        mDatabaseReference
            .child(sFavouriteCompaniesRef)
            .child(userToken)
            .child(companyId)
            .setValue(companyId)
    }

    override fun getCompaniesIdsFromDb(
        userToken: String
    ) = callbackFlow<BaseResponse<List<String>>> {
        mDatabaseReference
            .child(sFavouriteCompaniesRef)
            .child(userToken)
            .get()
            .addOnSuccessListener { idsSnapshot ->
                val ids = mutableListOf<String>()
                for (iteratorIdSnapshot in idsSnapshot.children) {
                    ids.add(iteratorIdSnapshot.key ?: "0")
                }
                trySend(
                    BaseResponse(
                        responseCode = Api.RESPONSE_OK,
                        responseData = ids
                    )
                )
            }
            .addOnFailureListener { trySend(BaseResponse(responseCode = Api.RESPONSE_NO_DATA)) }
        awaitClose()
    }
}