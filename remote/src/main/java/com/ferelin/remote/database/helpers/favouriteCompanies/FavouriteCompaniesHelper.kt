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

package com.ferelin.remote.database.helpers.favouriteCompanies

import com.ferelin.remote.base.BaseResponse
import kotlinx.coroutines.flow.Flow

/**
 * [FavouriteCompaniesHelper] provides methods for interacting with database companies
 * */
interface FavouriteCompaniesHelper {

    /**
     * Provides ability to erase a database id from cloud database
     * @param userToken is a user verification id that is used to access to correct node of cloud datastore.
     * @param companyId is a company id at local database that will be erased.
     */
    fun eraseCompanyIdFromRealtimeDb(userToken: String, companyId: String)

    /**
     * Provides ability to write a company id to cloud database.
     * @param userToken is a user verification id that is used to access to correct node of cloud datastore.
     * @param companyId is a company id at local database that will be saved.
     */
    fun cacheCompanyIdToRealtimeDb(userToken: String, companyId: String)

    /**
     * Provides ability to read user favourite companies ids from cloud database.
     * @param userToken is a user verification id that is used to access to correct node of cloud datastore.
     * @return [BaseResponse] with company ID and [Api] response code as flow.
     */
    fun getCompaniesIdsFromDb(userToken: String): Flow<BaseResponse<List<String>>>
}