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

package com.ferelin.local.dataStorage

import kotlinx.coroutines.flow.Flow

/**
 * Represents storage with key-value pairs of data.
 * */
interface DataStorage {

    /**
     * Observes for first time launch state
     *
     * @return flow of first time launch state
     * */
    suspend fun observeFirstTimeLaunch(): Flow<Boolean>

    /**
     * Caches first time launch state
     *
     * @param value is a new value of first time launch state
     * */
    suspend fun cacheFirstTimeLaunchState(value: Boolean)

    /**
     * Caches new search request
     *
     * @param searchRequest is a new search request that must be stored
     * */
    suspend fun cacheSearchRequest(searchRequest: String)

    /**
     * Observes for search requests set of data
     *
     * @return flow of cached search requests
     * */
    suspend fun observeSearchRequests(): Flow<Set<String>>

    /**
     * Erases search request from data storage
     *
     * @param searchRequest is a search request that must be erased
     * */
    suspend fun eraseSearchRequest(searchRequest: String)

    /**
     * Clears all search requests from data store
     * */
    suspend fun clearSearchRequests()
}