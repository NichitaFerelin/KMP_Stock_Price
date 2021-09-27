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

package com.ferelin.domain.interactors

import com.ferelin.domain.repositories.FirstLaunchRepo
import com.ferelin.shared.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

class FirstLaunchInteractor @Inject constructor(
    private val mFirstLaunchRepo: FirstLaunchRepo,
    private val mCoroutineContextProvider: CoroutineContextProvider,
    @Named("ExternalScope") private val mExternalScope: CoroutineScope
) {
    suspend fun getFirstTimeLaunch(): Boolean {
        return mFirstLaunchRepo.getFirstTimeLaunch()
    }

    suspend fun cacheFirstTimeLaunch(isFirstLaunch: Boolean) {
        mExternalScope.launch(mCoroutineContextProvider.IO) {
            mFirstLaunchRepo.cacheFirstTimeLaunch(isFirstLaunch)
        }
    }
}