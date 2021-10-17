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

import com.ferelin.domain.repositories.companies.CompaniesLoadState
import com.ferelin.domain.repositories.companies.CompaniesRemoteRepo
import com.ferelin.shared.DispatchersProvider
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class CompaniesRemoteRepoImpl @Inject constructor(
    private val mFirebaseReference: DatabaseReference,
    private val mDispatchersProvider: DispatchersProvider
) : CompaniesRemoteRepo {

    private companion object {
        const val sFavouriteCompaniesRef = "favourite-companies"
    }

    override suspend fun cacheCompanyIdToFavourites(
        userToken: String,
        companyId: Int
    ): Unit = withContext(mDispatchersProvider.IO) {
        Timber.d("cache to favourites (userToken = $userToken, companyId = $companyId)")

        mFirebaseReference
            .child(sFavouriteCompaniesRef)
            .child(userToken)
            .child(companyId.toString())
            .setValue(companyId)
    }

    override suspend fun eraseCompanyIdFromFavourites(
        userToken: String,
        companyId: Int
    ): Unit = withContext(mDispatchersProvider.IO) {
        Timber.d("erase from favourites (userToken = $userToken, companyId = $companyId)")

        mFirebaseReference
            .child(sFavouriteCompaniesRef)
            .child(userToken)
            .child(companyId.toString())
            .removeValue()
    }

    override suspend fun getFavouriteCompaniesIds(
        userToken: String
    ): Flow<CompaniesLoadState> = callbackFlow {
        Timber.d("get favourite companies ids")

        mFirebaseReference
            .child(sFavouriteCompaniesRef)
            .child(userToken)
            .get()
            .addOnCompleteListener { resultSnapshot ->
                if (resultSnapshot.isSuccessful && resultSnapshot.result != null) {
                    val favouriteCompaniesIds = mutableListOf<Int>()
                    val idsSnapshot = resultSnapshot.result!!

                    for (idSnapshot in idsSnapshot.children) {
                        favouriteCompaniesIds.add(idSnapshot.key?.toInt() ?: 0)
                    }

                    trySend(
                        CompaniesLoadState.Loaded(
                            companies = favouriteCompaniesIds.toList()
                        )
                    )
                } else {
                    trySend(CompaniesLoadState.Error)
                }
            }
        awaitClose()
    }
        .take(1)
        .flowOn(mDispatchersProvider.IO)

    override suspend fun clearCompanies(userToken: String) {
        mFirebaseReference
            .child(sFavouriteCompaniesRef)
            .child(userToken)
            .removeValue()
    }
}