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

import com.ferelin.domain.entities.SearchRequest
import com.ferelin.shared.LoadState
import kotlinx.coroutines.flow.StateFlow

/**
 * [SearchRequestsInteractor] provides ability to interact with search requests data
 * */
interface SearchRequestsInteractor {

    /**
     * Provides a state with actual search requests
     * */
    val searchRequestsState: StateFlow<LoadState<List<SearchRequest>>>

    /**
     * Caches new search request
     * @param searchText is a search request that must be cached
     * */
    suspend fun cache(searchText: String)

    /**
     * Allows to get all cached search requests
     * @return list of cached search requests
     * */
    suspend fun getAll(): List<SearchRequest>

    /**
     * Allows to get all popular search requests
     * @return list of popular search requests
     * */
    suspend fun getAllPopular(): List<SearchRequest>

    /**
     * Erases user data such a search requests
     * */
    suspend fun eraseUserData()
}