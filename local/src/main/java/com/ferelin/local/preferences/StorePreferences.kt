package com.ferelin.local.preferences

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

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [StorePreferences] providing:
 *  - Access to search requests history. @property [mSearchRequestsHistoryKey]
 *  - Access to first time launch state. @property [mFirstTimeLaunchKey]
 */

@Singleton
open class StorePreferences @Inject constructor(
    @ApplicationContext private val mContext: Context
) : StorePreferencesHelper {

    private val Context.dataStorePreferences by preferencesDataStore(name = "stockspirce.preferences.db")

    private val mSearchRequestsHistoryKey = stringSetPreferencesKey("history-key")
    private val mFirstTimeLaunchKey = booleanPreferencesKey("welcome-key")

    override fun getSearchesHistory(): Flow<Set<String>> {
        return mContext.dataStorePreferences.data.map {
            it[mSearchRequestsHistoryKey] ?: emptySet()
        }
    }

    override suspend fun setSearchesHistory(requests: Set<String>) {
        mContext.dataStorePreferences.edit {
            it[mSearchRequestsHistoryKey] = requests
        }
    }

    override suspend fun setFirstTimeLaunchState(boolean: Boolean) {
        mContext.dataStorePreferences.edit {
            it[mFirstTimeLaunchKey] = boolean
        }
    }

    override fun getFirstTimeLaunchState(): Flow<Boolean?> {
        return mContext.dataStorePreferences.data.map {
            it[mFirstTimeLaunchKey]
        }
    }
}