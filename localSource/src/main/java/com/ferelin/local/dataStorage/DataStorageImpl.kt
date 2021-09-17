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

package com.ferelin.local.dataStorage

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class DataStorageImpl @Inject constructor(
    private val mDataStore: DataStore<Preferences>
) : DataStorage {

    private companion object {
        val sFirstTimeLaunchKey = booleanPreferencesKey("first-launch")
        val sSearchRequestsKey = stringSetPreferencesKey("search-requests")
    }

    override suspend fun cacheFirstTimeLaunchState(value: Boolean) {
        mDataStore.edit {
            it[sFirstTimeLaunchKey] = value
        }
    }

    override suspend fun observeFirstTimeLaunch(): Flow<Boolean> {
        return mDataStore.data.map {
            it[sFirstTimeLaunchKey] ?: true
        }
    }

    override suspend fun cacheSearchRequest(searchRequest: String) {
        mDataStore.edit {
            it[sSearchRequestsKey]?.toMutableSet()?.add(searchRequest)
        }
    }

    override suspend fun observeSearchRequests(): Flow<Set<String>> {
        return mDataStore.data.map {
            it[sSearchRequestsKey] ?: emptySet()
        }
    }

    override suspend fun eraseSearchRequest(searchRequest: String) {
        mDataStore.edit {
            it[sSearchRequestsKey]?.toMutableSet()?.remove(searchRequest)
        }
    }

    override suspend fun clearSearchRequests() {
        mDataStore.edit {
            it[sSearchRequestsKey] = emptySet()
        }
    }
}