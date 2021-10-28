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

package com.ferelin.data_local.reposirotires

import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.ferelin.data_local.preferences.PreferencesProvider
import com.ferelin.data_local.sources.PopularRequestsSource
import com.ferelin.domain.repositories.searchRequests.SearchRequestsLocalRepo
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class SearchRequestsRepoImpl @Inject constructor(
    private val preferencesProvider: PreferencesProvider,
    private val dispatchersProvider: DispatchersProvider
) : SearchRequestsLocalRepo {

    private companion object {
        val searchRequestsKey = stringSetPreferencesKey("search-requests")
    }

    override suspend fun insert(searchRequests: Set<String>): Unit =
        withContext(dispatchersProvider.IO) {
            Timber.d("insert (search requests size = ${searchRequests.size})")

            preferencesProvider.dataStore.edit {
                it[searchRequestsKey] = searchRequests
            }
        }

    override suspend fun getAll(): Set<String> =
        withContext(dispatchersProvider.IO) {
            Timber.d("get all")

            return@withContext preferencesProvider.dataStore.data.map {
                it[searchRequestsKey]
            }.firstOrNull() ?: emptySet()
        }

    override suspend fun getAllPopular(): Set<String> {
        Timber.d("get all popular")

        return PopularRequestsSource.popularSearchRequests
    }

    override suspend fun eraseAll(): Unit =
        withContext(dispatchersProvider.IO) {
            Timber.d("erase all")

            preferencesProvider.dataStore.edit {
                it[searchRequestsKey] = setOf()
            }
        }
}