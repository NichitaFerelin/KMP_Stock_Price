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

package com.ferelin.remote.database.helpers.searchRequests

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.utils.Api
import kotlinx.coroutines.flow.Flow

interface SearchRequestsHelper {

    /**
     * Provides ability to save search requests to cloud database.
     * @param userId is a user verification id that is used to access to correct node of cloud datastore.
     * @param searchRequest is a search requests that will be saved.
     */
    fun writeSearchRequestToDb(userId: String, searchRequest: String)

    /**
     * Provides ability to save a list of search requests to cloud database.
     * @param userId is a user verification id that is used to access to correct node of cloud datastore.
     * @param searchRequests is a search requests list that will be saved.
     */
    fun writeSearchRequestsToDb(userId: String, searchRequests: List<String>)

    /**
     * Provides ability to read a search history from cloud database.
     * @param userId is a user verification id that is used to access to correct node of cloud datastore.
     * @return [BaseResponse] with search request and [Api] response code as flow.
     */
    fun readSearchRequestsFromDb(userId: String): Flow<BaseResponse<String?>>

    /**
     * Provides ability to erase a search request from cloud database.
     * @param userId is a user verification id that is used to access to correct node of cloud datastore.
     * @param searchRequest is a search request that will be erased.
     */
    fun eraseSearchRequestFromDb(userId: String, searchRequest: String)
}