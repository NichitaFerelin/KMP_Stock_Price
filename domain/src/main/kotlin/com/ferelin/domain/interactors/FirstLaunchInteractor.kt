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
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named

class FirstLaunchInteractor @Inject constructor(
    private val mFirstLaunchRepo: FirstLaunchRepo,
    private val mDispatchersProvider: DispatchersProvider,
    @Named("ExternalScope") private val mExternalScope: CoroutineScope
) {
    private companion object {
        const val DEFAULT_FIRST_LAUNCH_STATE = true
    }

    suspend fun get(): Boolean {
        return mFirstLaunchRepo.get() ?: DEFAULT_FIRST_LAUNCH_STATE
    }

    suspend fun cache(isFirstLaunch: Boolean) {
        mExternalScope.launch(mDispatchersProvider.IO) {
            mFirstLaunchRepo.cache(isFirstLaunch)
        }
    }
}