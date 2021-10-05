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

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.ferelin.domain.repositories.FirstLaunchRepo
import com.ferelin.local.utils.PreferencesProvider
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirstLaunchRepoImpl @Inject constructor(
    private val mPreferencesProvider: PreferencesProvider,
    private val mDispatchersProvider: DispatchersProvider
) : FirstLaunchRepo {

    private companion object {
        val sFirstTimeLaunchKey = booleanPreferencesKey("first-launch")
    }

    override suspend fun getFirstTimeLaunch(): Boolean =
        withContext(mDispatchersProvider.IO) {
            return@withContext mPreferencesProvider.dataStore.data.map {
                it[sFirstTimeLaunchKey]
            }.firstOrNull() ?: true
        }

    override suspend fun cacheFirstTimeLaunch(isFirstLaunch: Boolean): Unit =
        withContext(mDispatchersProvider.IO) {
            mPreferencesProvider.dataStore.edit {
                it[sFirstTimeLaunchKey] = isFirstLaunch
            }
        }
}