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

package com.ferelin.domain.interactors.searchRequests

import com.ferelin.shared.LoadState
import kotlinx.coroutines.flow.StateFlow

/**
 * [SearchRequestsInteractor] provides ability to interact with search requests data
 * */
interface SearchRequestsInteractor {

    /**
     * Provides a state with actual search requests
     * */
    val searchRequestsState: StateFlow<LoadState<Set<String>>>

    /**
     * Caches new search request
     * @param searchRequest is a search request that must be cached
     * */
    suspend fun cache(searchRequest: String)

    /**
     * Allows to get all cached search requests
     * @return set of cached search requests
     * */
    suspend fun getAll(): Set<String>

    /**
     * Allows to get all popular search requests
     * @return set of popular search requests
     * */
    suspend fun getAllPopular(): Set<String>

    /**
     * Erases user data such a search requests
     * */
    suspend fun eraseUserData()
}