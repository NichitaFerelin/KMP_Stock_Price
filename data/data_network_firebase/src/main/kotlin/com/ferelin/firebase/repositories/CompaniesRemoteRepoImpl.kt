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

import com.ferelin.domain.repositories.companies.CompaniesRemoteRepo
import com.ferelin.shared.DispatchersProvider
import com.ferelin.shared.LoadState
import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class CompaniesRemoteRepoImpl @Inject constructor(
    private val firebaseReference: DatabaseReference,
    private val dispatchersProvider: DispatchersProvider
) : CompaniesRemoteRepo {

    private companion object {
        const val FAVOURITE_COMPANIES_REF = "favourite-companies"
    }

    override suspend fun insertBy(
        userToken: String,
        companyId: Int
    ): Unit = withContext(dispatchersProvider.IO) {
        Timber.d("insert by (company id = $companyId)")

        firebaseReference
            .child(FAVOURITE_COMPANIES_REF)
            .child(userToken)
            .child(companyId.toString())
            .setValue(companyId)
    }

    override suspend fun loadAll(
        userToken: String
    ) = callbackFlow<LoadState<List<Int>>> {
        Timber.d("get all")

        firebaseReference
            .child(FAVOURITE_COMPANIES_REF)
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
                        LoadState.Prepared(
                            data = favouriteCompaniesIds.toList()
                        )
                    )
                } else {
                    trySend(LoadState.Error())
                }
            }
        awaitClose()
    }.flowOn(dispatchersProvider.IO)

    override suspend fun eraseAll(userToken: String) {
        Timber.d("erase all")

        firebaseReference
            .child(FAVOURITE_COMPANIES_REF)
            .child(userToken)
            .removeValue()
    }

    override suspend fun eraseBy(
        userToken: String,
        companyId: Int
    ): Unit = withContext(dispatchersProvider.IO) {
        Timber.d("erase by (company id = $companyId)")

        firebaseReference
            .child(FAVOURITE_COMPANIES_REF)
            .child(userToken)
            .child(companyId.toString())
            .removeValue()
    }
}