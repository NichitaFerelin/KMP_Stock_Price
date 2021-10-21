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

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.ferelin.domain.repositories.FirstLaunchRepo
import com.ferelin.data_local.utils.PreferencesProvider
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import timber.log.Timber
import javax.inject.Inject

class FirstLaunchRepoImpl @Inject constructor(
    private val preferencesProvider: PreferencesProvider,
    private val dispatchersProvider: DispatchersProvider
) : FirstLaunchRepo {

    private companion object {
        val FIRST_TIME_LAUNCH_KEY = booleanPreferencesKey("first-launch")
    }

    override suspend fun get(): Boolean? =
        withContext(dispatchersProvider.IO) {
            Timber.d("get")

            return@withContext preferencesProvider.dataStore.data.map {
                it[FIRST_TIME_LAUNCH_KEY]
            }.firstOrNull()
        }

    override suspend fun cache(isFirstLaunch: Boolean): Unit =
        withContext(dispatchersProvider.IO) {
            Timber.d("cache (isFirstLaunch = $isFirstLaunch)")

            preferencesProvider.dataStore.edit {
                it[FIRST_TIME_LAUNCH_KEY] = isFirstLaunch
            }
        }
}