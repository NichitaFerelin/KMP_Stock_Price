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

package com.ferelin.stockprice.dataInteractor.interactorHelpers

import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.StockHistory
import com.ferelin.repository.adaptiveModels.CompanyNews
import com.ferelin.stockprice.dataInteractor.DataInteractor
import com.ferelin.stockprice.dataInteractor.workers.companies.CompaniesMediator
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.Flow

/**
 * Methods for interacting with companies via [DataInteractor].
 * @see [CompaniesMediator] to get info about how methods works
 * */
interface CompaniesHelper {

    suspend fun addCompanyToFavourites(company: AdaptiveCompany, ignoreError: Boolean = false)

    suspend fun removeCompanyFromFavourites(company: AdaptiveCompany)

    suspend fun loadStockHistory(symbol: String): Flow<DataNotificator<StockHistory?>>

    suspend fun loadCompanyNews(symbol: String): Flow<DataNotificator<CompanyNews?>>

    suspend fun sendRequestToLoadStockPrice(
        symbol: String,
        position: Int,
        isImportant: Boolean
    )
}