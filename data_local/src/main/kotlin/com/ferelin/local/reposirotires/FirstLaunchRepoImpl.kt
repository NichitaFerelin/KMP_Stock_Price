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
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.ferelin.domain.repositories.FirstLaunchRepo
import com.ferelin.shared.CoroutineContextProvider
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirstLaunchRepoImpl @Inject constructor(
    private val mDataStore: DataStore<Preferences>,
    private val mCoroutineContextProvider: CoroutineContextProvider
) : FirstLaunchRepo {

    private companion object {
        val sFirstTimeLaunchKey = booleanPreferencesKey("first-launch")
    }

    override suspend fun getFirstTimeLaunch(): Boolean =
        withContext(mCoroutineContextProvider.IO) {
            return@withContext mDataStore.data.map {
                it[sFirstTimeLaunchKey]
            }.firstOrNull() ?: true
        }

    override suspend fun cacheFirstTimeLaunch(isFirstLaunch: Boolean): Unit =
        withContext(mCoroutineContextProvider.IO) {
            mDataStore.edit {
                it[sFirstTimeLaunchKey] = isFirstLaunch
            }
        }
}