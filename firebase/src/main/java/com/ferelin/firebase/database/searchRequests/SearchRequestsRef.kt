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

package com.ferelin.firebase.database.searchRequests

/**
 * [SearchRequestsRef] provides methods for interacting with database search requests
 * */
interface SearchRequestsRef {

    /**
     * Provides ability to save search requests to cloud database.
     *
     * @param userToken is a user verification id that is used to access
     * to correct node of cloud datastore.
     * @param searchRequest is a search requests that will be saved.
     */
    suspend fun cacheSearchRequest(userToken: String, searchRequest: String)

    /**
     * Provides ability to erase a search request from cloud database.
     *
     * @param userToken is a user verification id that is used to access
     * to correct node of cloud datastore.
     * @param searchRequest is a search request that will be erased.
     */
    suspend fun eraseSearchRequest(userToken: String, searchRequest: String)

    /**
     * Provides ability to read a search history from cloud database.
     *
     * @param userToken is a user verification id that is used to access
     * to correct node of cloud datastore.
     * @return list of search requests
     */
    suspend fun loadSearchRequests(userToken: String): List<String>
}