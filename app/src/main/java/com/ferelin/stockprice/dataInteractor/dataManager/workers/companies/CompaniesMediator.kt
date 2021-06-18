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

package com.ferelin.stockprice.dataInteractor.dataManager.workers.companies

import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveWebSocketPrice
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.repository.utils.RepositoryResponse
import kotlinx.coroutines.flow.Flow

interface CompaniesMediator {

    fun onCompaniesDataPrepared(companies: List<AdaptiveCompany>)

    fun subscribeItemsOnLiveTimeUpdates()

    suspend fun addCompanyToFavourites(
        company: AdaptiveCompany,
        ignoreError: Boolean = false
    )

    suspend fun removeCompanyFromFavourites(company: AdaptiveCompany)

    suspend fun onLiveTimePriceChanged(response: RepositoryResponse.Success<AdaptiveWebSocketPrice>)

    fun getCompany(symbol: String): AdaptiveCompany?

    suspend fun loadStockCandlesFromNetwork(
        symbol: String,
        onError: suspend (RepositoryMessages, String) -> Unit
    ): Flow<AdaptiveCompany>

    suspend fun loadCompanyNewsFromNetwork(
        symbol: String,
        onError: suspend (RepositoryMessages, String) -> Unit
    ): Flow<AdaptiveCompany>

    suspend fun loadCompanyQuoteFromNetwork(
        symbol: String,
        position: Int,
        isImportant: Boolean
    ): Flow<AdaptiveCompany>
}