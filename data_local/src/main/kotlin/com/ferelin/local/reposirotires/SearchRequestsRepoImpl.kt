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

package com.ferelin.local.reposirotires

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.ferelin.domain.repositories.searchRequests.SearchRequestsLocalRepo
import com.ferelin.local.utils.PopularRequestsSource
import com.ferelin.shared.CoroutineContextProvider
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Singleton

@Singleton
class SearchRequestsRepoImpl(
    private val mDataStore: DataStore<Preferences>,
    private val mCoroutineContextProvider: CoroutineContextProvider
) : SearchRequestsLocalRepo {

    private companion object {
        val sSearchRequestsKey = stringSetPreferencesKey("search-requests")
    }

    override suspend fun cacheSearchRequest(searchRequest: String): Unit =
        withContext(mCoroutineContextProvider.IO) {
            mDataStore.edit {
                val source = it[sSearchRequestsKey]?.toMutableSet() ?: mutableSetOf()
                source.add(searchRequest)
                it[sSearchRequestsKey] = source
            }
        }

    override suspend fun eraseSearchRequest(searchRequest: String): Unit =
        withContext(mCoroutineContextProvider.IO) {
            mDataStore.edit {
                it[sSearchRequestsKey]?.toMutableSet()?.let { sourceRequests ->
                    sourceRequests.remove(searchRequest)
                    it[sSearchRequestsKey] = sourceRequests
                }
            }
        }

    override suspend fun getSearchRequests(): List<String> =
        withContext(mCoroutineContextProvider.IO) {
            return@withContext mDataStore.data.map {
                it[sSearchRequestsKey]?.toList()
            }.firstOrNull() ?: emptyList()
        }

    override suspend fun getPopularSearchRequests(): List<String> {
        return PopularRequestsSource.popularSearchRequests
    }

    override suspend fun clearSearchRequests(): Unit =
        withContext(mCoroutineContextProvider.IO) {
            mDataStore.edit {
                it[sSearchRequestsKey] = emptySet()
            }
        }
}