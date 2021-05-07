package com.ferelin.repository

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

import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.repository.utils.Time
import kotlinx.coroutines.flow.Flow

interface RepositoryManagerHelper {

    fun loadStockCandles(
        symbol: String,
        /*
        * Api has a limit on receiving data a maximum of year ago
        * */
        from: Long = Time.convertMillisForRequest(System.currentTimeMillis() - Time.ONE_YEAR),
        to: Long = Time.convertMillisForRequest(System.currentTimeMillis()),
        // Days format
        resolution: String = "D"
    ): Flow<RepositoryResponse<AdaptiveCompanyHistory>>

    fun loadCompanyProfile(symbol: String): Flow<RepositoryResponse<AdaptiveCompanyProfile>>

    fun loadStockSymbols(): Flow<RepositoryResponse<AdaptiveStocksSymbols>>

    fun loadCompanyNews(
        symbol: String,
        /*
        * Api has a limit on receiving data a maximum of year ago
        * */
        from: String = Time.getYearAgoDateForRequest(),
        to: String = Time.getCurrentDateForRequest()
    ): Flow<RepositoryResponse<AdaptiveCompanyNews>>

    fun openWebSocketConnection(): Flow<RepositoryResponse<AdaptiveWebSocketPrice>>

    fun invalidateWebSocketConnection()

    fun loadCompanyQuote(
        symbol: String,
        position: Int,
        isImportant: Boolean
    ): Flow<RepositoryResponse<AdaptiveCompanyDayData>>

    fun saveCompanyData(adaptiveCompany: AdaptiveCompany)

    fun getAllCompanies(): Flow<RepositoryResponse<List<AdaptiveCompany>>>

    fun subscribeItemToLiveTimeUpdates(symbol: String, openPrice: Double)

    fun unsubscribeItemFromLiveTimeUpdates(symbol: String)

    fun getSearchesHistory(): Flow<RepositoryResponse<List<AdaptiveSearchRequest>>>

    suspend fun setSearchesHistory(requests: List<AdaptiveSearchRequest>)

    fun getFirstTimeLaunchState() : Flow<RepositoryResponse<Boolean>>

    suspend fun setFirstTimeLaunchState(state: Boolean)
}