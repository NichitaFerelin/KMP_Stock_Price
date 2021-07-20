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

/**
 * [SearchRequestsHelper] provides methods for interacting with database search requests
 * */
interface SearchRequestsHelper {

    /**
     * Provides ability to save search requests to cloud database.
     * @param userToken is a user verification id that is used to access to correct node of cloud datastore.
     * @param searchRequest is a search requests that will be saved.
     */
    fun cacheSearchRequestToDb(userToken: String, searchRequestId: String, searchRequest: String)

    /**
     * Provides ability to erase a search request from cloud database.
     * @param userToken is a user verification id that is used to access to correct node of cloud datastore.
     * @param searchRequestId is a search request that will be erased.
     */
    fun eraseSearchRequestFromDb(userToken: String, searchRequestId: String)

    /**
     * Provides ability to read a search history from cloud database.
     * @param userToken is a user verification id that is used to access to correct node of cloud datastore.
     * @return [BaseResponse] with search request and [Api] response code as flow.
     */
    fun getSearchRequestsFromDb(userToken: String): Flow<BaseResponse<HashMap<Int, String>>>
}