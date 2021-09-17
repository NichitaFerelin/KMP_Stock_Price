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

package com.ferelin.firebase.database.favouriteCompanies

import com.google.firebase.database.DatabaseReference
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavouriteCompaniesRefImpl @Inject constructor(
    private val mFirebaseReference: DatabaseReference
) : FavouriteCompaniesRef {

    private companion object {
        const val sFavouriteCompaniesRef = "favourite-companies"
    }

    override suspend fun eraseFromFavourites(userToken: String, companyId: String) {
        mFirebaseReference
            .child(sFavouriteCompaniesRef)
            .child(userToken)
            .child(companyId)
            .removeValue()
    }

    override suspend fun cacheToFavourites(userToken: String, companyId: String) {
        mFirebaseReference
            .child(sFavouriteCompaniesRef)
            .child(userToken)
            .child(companyId)
            .setValue(companyId)
    }

    override suspend fun loadFavourites(userToken: String): List<String> {
        val resultSnapshot = mFirebaseReference
            .child(sFavouriteCompaniesRef)
            .child(userToken)
            .get()

        return if (resultSnapshot.isSuccessful && resultSnapshot.result != null) {
            val favouriteCompaniesIds = mutableListOf<String>()
            val idsSnapshot = resultSnapshot.result!!

            for (idSnapshot in idsSnapshot.children) {
                favouriteCompaniesIds.add(idSnapshot.key ?: "0")
            }

            favouriteCompaniesIds
        } else {
            emptyList()
        }
    }
}