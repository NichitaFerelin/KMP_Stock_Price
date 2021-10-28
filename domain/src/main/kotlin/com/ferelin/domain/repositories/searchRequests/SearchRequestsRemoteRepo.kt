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

package com.ferelin.domain.repositories.searchRequests

import com.ferelin.shared.LoadState
import kotlinx.coroutines.flow.Flow

/**
 * [SearchRequestsRemoteRepo] allows to interact with cloud database by
 * special user token
 * */
interface SearchRequestsRemoteRepo {

    /**
     * Inserts user search request to cloud database
     * @param userToken is an user token by which search request will be inserted
     * @param searchRequest is a search request need to be cached
     * */
    suspend fun insert(userToken: String, searchRequest: String)

    /**
     * Loads all search requests from cloud database
     * @param userToken is an user token by which need to load search requests
     * @return flow of [LoadState] with list of all user search requests
     * */
    suspend fun loadAll(userToken: String): Flow<LoadState<Set<String>>>

    /**
     * Erases all user search requests from cloud database
     * @param userToken is an user token by which need to erase all search requests
     * */
    suspend fun eraseAll(userToken: String)

    /**
     * Erase user search requests from cloud database
     * @param userToken is an user token by which need to erase search request
     * @param searchRequest is a search request that need to be erased
     * */
    suspend fun erase(userToken: String, searchRequest: String)
}