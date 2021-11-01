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

package com.ferelin.domain.useCases.firstLaunch

import com.ferelin.domain.repositories.FirstLaunchRepo
import javax.inject.Inject

/**
 * [FirstLaunchGetUseCase] allows to interact with first time application launch
 * */
class FirstLaunchGetUseCase @Inject constructor(
    private val firstLaunchRepo: FirstLaunchRepo,
) {
    companion object {
        private const val defaultFirstLaunchState = true
    }

    /**
     * Allows to get first time application launch
     * @return first time application launch
     * */
    suspend fun get(): Boolean {
        return firstLaunchRepo.get() ?: defaultFirstLaunchState
    }
}