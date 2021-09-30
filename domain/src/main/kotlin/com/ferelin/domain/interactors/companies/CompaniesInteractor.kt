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
import kotlinx.coroutines.flow.StateFlow

interface CompaniesInteractor : NetworkListener {

    val companyWithStockPriceChanges: SharedFlow<CompanyWithStockPrice>

    suspend fun getAll(): List<CompanyWithStockPrice>

    suspend fun getAllFavourites(): List<CompanyWithStockPrice>

    suspend fun addCompanyToFavourites(company: Company)

    suspend fun removeCompanyFromFavourites(company: Company)

    fun observeFavouriteCompaniesUpdates(): SharedFlow<Company>
}