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

package com.ferelin.repository.helpers.local

import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.repository.utils.RepositoryResponse

interface StorePreferencesHelper {

    suspend fun clearLocalSearchRequestsHistory()

    suspend fun getSearchesHistoryFromLocalDb(): RepositoryResponse<List<AdaptiveSearchRequest>>

    suspend fun cacheSearchRequestsHistoryToLocalDb(requests: List<AdaptiveSearchRequest>)

    suspend fun getFirstTimeLaunchState(): RepositoryResponse<Boolean>

    suspend fun setFirstTimeLaunchState(state: Boolean)

    suspend fun getUserRegisterState() : Boolean?

    suspend fun setUserRegisterState(state: Boolean)
}