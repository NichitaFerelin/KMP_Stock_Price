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

package com.ferelin.stockprice.dataInteractor.dataManager

import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.dataInteractor.dataManager.workers.errors.ErrorsWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.messages.MessagesWorker
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.actionHolder.ActionHolder
import kotlinx.coroutines.flow.StateFlow

interface DataMediator :
    DataMediatorStates,
    ErrorsWorker,
    MessagesWorker {

    fun onCommonCompaniesDataPrepared(companies: List<AdaptiveCompany>)

    fun subscribeItemsOnLiveTimeUpdates()

    suspend fun onAddFavouriteCompany(
        company: AdaptiveCompany,
        ignoreError: Boolean = false
    ): Boolean

    suspend fun onRemoveFavouriteCompany(company: AdaptiveCompany)

    fun onSearchRequestsHistoryPrepared(searches: List<AdaptiveSearchRequest>)

    suspend fun onStockCandlesLoaded(response: RepositoryResponse.Success<AdaptiveCompanyHistory>)

    suspend fun onCompanyNewsLoaded(response: RepositoryResponse.Success<AdaptiveCompanyNews>)

    suspend fun onCompanyQuoteLoaded(response: RepositoryResponse.Success<AdaptiveCompanyDayData>)

    suspend fun onLiveTimePriceChanged(response: RepositoryResponse.Success<AdaptiveWebSocketPrice>)

    suspend fun getMessagesStateForLoginFromCache(
        associatedUserLogin: String
    ): StateFlow<DataNotificator<AdaptiveMessagesHolder>>

    suspend fun onLogStateChanged(isLogged: Boolean)

    suspend fun cacheNewSearchRequest(searchText: String): List<ActionHolder<String>>

    suspend fun clearSearchRequests()

    fun getCompany(symbol: String): AdaptiveCompany?
}