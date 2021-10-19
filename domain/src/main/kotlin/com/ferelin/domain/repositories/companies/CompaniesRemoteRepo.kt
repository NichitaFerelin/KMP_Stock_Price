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

package com.ferelin.domain.repositories.companies

import com.ferelin.shared.LoadState
import kotlinx.coroutines.flow.Flow

/**
 * [CompaniesRemoteRepo] allows to interact with remote companies repository
 * by special user token
 * */
interface CompaniesRemoteRepo {

    /**
     * Inserts company id to cloud database.
     * Directly used to store user favourite companies ids.
     * @param userToken is an user token by which company id will be inserted to cloud database
     * @param companyId is a company that will be inserted to cloud db
     * */
    suspend fun insertBy(userToken: String, companyId: Int)

    /**
     * Loads companies ids from cloud database
     * @param userToken is an user token by which companies will be loaded from cloud database
     * @return flow of [LoadState] with cloud companies ids if [LoadState] is successful
     * */
    suspend fun loadAll(userToken: String): Flow<LoadState<List<Int>>>

    /**
     * Erases all user companies ids from cloud database
     * @param userToken is an user token by which companies will be erased
     * */
    suspend fun eraseAll(userToken: String)

    /**
     * Erases user company id from cloud database
     * @param userToken is an user token by which company id will be erased from cloud database
     * @param companyId is a company that will be erased from cloud database
     * */
    suspend fun eraseBy(userToken: String, companyId: Int)
}