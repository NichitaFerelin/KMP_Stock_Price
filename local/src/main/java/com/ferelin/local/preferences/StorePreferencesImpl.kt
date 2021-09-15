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

package com.ferelin.local.preferences

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [StorePreferencesImpl] provides:
 *  - Access to search requests history
 *  - Access to first time launch state
 */
@Singleton
open class StorePreferencesImpl @Inject constructor(
    private val mContext: Context
) : StorePreferences {

    private val Context.dataStorePreferences by preferencesDataStore(name = "stockspirce.preferences.db")

    private val mFirstTimeLaunchKey = booleanPreferencesKey("welcome-key")
    private val mUserNumberKey = stringPreferencesKey("user-number-key")

    override suspend fun cacheFirstTimeLaunchState(boolean: Boolean) {
        mContext.dataStorePreferences.edit {
            it[mFirstTimeLaunchKey] = boolean
        }
    }

    override suspend fun getFirstTimeLaunchState(): Boolean? {
        return mContext.dataStorePreferences.data.map {
            it[mFirstTimeLaunchKey]
        }.firstOrNull()
    }

    override suspend fun cacheUserNumber(number: String) {
        mContext.dataStorePreferences.edit {
            it[mUserNumberKey] = number
        }
    }

    override suspend fun getUserNumber(): String? {
        return mContext.dataStorePreferences.data.map {
            it[mUserNumberKey]
        }.firstOrNull()
    }
}