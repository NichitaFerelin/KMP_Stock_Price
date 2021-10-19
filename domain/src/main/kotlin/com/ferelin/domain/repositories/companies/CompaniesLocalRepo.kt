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

import com.ferelin.domain.entities.Company
import com.ferelin.domain.entities.CompanyWithStockPrice

/**
 * [CompaniesLocalRepo] allows to interact with local database
 * */
interface CompaniesLocalRepo {

    suspend fun insertAll(companies: List<Company>)

    suspend fun getAll(): List<CompanyWithStockPrice>

    suspend fun getAllFavourites(): List<CompanyWithStockPrice>

    /**
     * Rollbacks companies properties (such a 'isFavourite') to default (isFavourite = false)
     * */
    suspend fun rollbackToDefault()

    /**
     * Updates company property 'isFavourite' and 'addedByIndex'
     * @param companyId is a company id for which need to update fields
     * @param isFavourite is a new value for property
     * @param addedByIndex is a new value for property
     * */
    suspend fun updateIsFavourite(companyId: Int, isFavourite: Boolean, addedByIndex: Int = 0)
}