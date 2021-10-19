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

package com.ferelin.domain.interactors.companies

import com.ferelin.domain.entities.Company
import com.ferelin.domain.entities.CompanyWithStockPrice
import com.ferelin.shared.NetworkListener
import kotlinx.coroutines.flow.SharedFlow

/**
 * [CompaniesInteractor] provides ability to interact with companies data
 * */
interface CompaniesInteractor : NetworkListener {

    /**
     * Field that allows to observe company data updates such a stock price
     * */
    val companyWithStockPriceUpdates: SharedFlow<CompanyWithStockPrice>

    /**
     * Field that allows to observe company 'isFavourite' property
     * */
    val favouriteCompaniesUpdates: SharedFlow<CompanyWithStockPrice>

    /**
     * Allows to get all cached companies
     * @return list of cached companies with stock price
     * */
    suspend fun getAll(): List<CompanyWithStockPrice>

    /**
     * Allows to get all user favourite companies
     * @return list of favourite companies with stock price
     * */
    suspend fun getAllFavourites(): List<CompanyWithStockPrice>

    /**
     * Adds company to favourites by id
     * @param companyId is an id of company to add
     * */
    suspend fun addCompanyToFavourites(companyId: Int)

    /**
     * Adds company to favourites
     * @param company is a company to add
     * */
    suspend fun addCompanyToFavourites(company: Company)

    /**
     * Erases company from favourites by id
     * @param companyId id an id of company to erase
     * */
    suspend fun eraseCompanyFromFavourites(companyId: Int)

    /**
     * Erases company from favourites
     * @param is a company to erase
     * */
    suspend fun eraseCompanyFromFavourites(company: Company)

    /**
     * Erases user data such favourite companies
     * */
    suspend fun eraseUserData()
}