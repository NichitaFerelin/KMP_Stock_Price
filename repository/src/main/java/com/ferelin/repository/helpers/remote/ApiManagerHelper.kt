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

package com.ferelin.repository.helpers.remote

import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.repository.utils.Time
import kotlinx.coroutines.flow.Flow

/**
 * [ApiManagerHelper] provides methods for network interacting with api.
 * */
interface ApiManagerHelper {

    fun loadStockHistory(
        symbol: String,
        /*
        * Api has a limit on receiving data a maximum of year ago
        * */
        from: Long = Time.convertMillisForRequest(System.currentTimeMillis() - Time.ONE_YEAR),
        to: Long = Time.convertMillisForRequest(System.currentTimeMillis()),
        // Time format
        resolution: String = "D"
    ): RepositoryResponse<StockHistory>

    fun loadCompanyProfile(symbol: String): RepositoryResponse<CompanyProfile>

    fun loadStockSymbols(): RepositoryResponse<AdaptiveStocksSymbols>

    fun loadCompanyNews(
        symbol: String,
        /*
        * Api has a limit on receiving data a maximum of year ago
        * */
        from: String = Time.getYearAgoDateForRequest(),
        to: String = Time.getCurrentDateForRequest()
    ): RepositoryResponse<CompanyNews>

    fun sendRequestToLoadPrice(
        symbol: String,
        position: Int,
        isImportant: Boolean
    )

    fun getStockPriceResponseState(): Flow<RepositoryResponse<StockPrice>>
}