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

package com.ferelin.remote.networkApi

import com.ferelin.remote.utils.BaseResponse
import com.ferelin.remote.networkApi.entities.ActualStockPriceResponse
import com.ferelin.remote.networkApi.entities.CompanyNewsResponse
import com.ferelin.remote.networkApi.entities.StockPriceHistoryResponse
import kotlinx.coroutines.flow.Flow

/**
 * Provides methods to use network API
 * */
interface NetworkApi {

    /**
     * Requests to load stock price changes history
     *
     * @param symbol is a company symbol for which stock price change history need to be loaded
     * @param from represents time-millis string starting from which need to return stock history
     * @param to represents time-millis string ending to which need to return stock history
     * @param resolution is type in which need to return data. By day / week / month, etc.
     * @return server response as [BaseResponse] object with [StockPriceHistoryResponse] data
     * */
    suspend fun loadPriceChangesHistory(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): BaseResponse<StockPriceHistoryResponse>

    /**
     * Requests to load company news
     *
     * @param symbol is a company symbol for which news need to be loaded
     * @param from represents time-millis string starting from which need to return company news
     * @param to represents time-millis string ending to which need to return company news
     * @return server response as [BaseResponse] object with [CompanyNewsResponse] data-list
     * */
    suspend fun loadCompanyNews(
        symbol: String,
        from: String,
        to: String
    ): BaseResponse<List<CompanyNewsResponse>>

    /**
     * Requests to load company actual stock price
     *
     * @param symbol is a company symbol for which quote need to load
     * @param keyPosition is a key-position according to which it is decided whether to
     *  execute the request or not
     * @param isImportant forces the request to be executed ignoring limiter restrictions
     * */
    suspend fun loadActualStockPriceWithLimiter(
        symbol: String,
        keyPosition: Int,
        isImportant: Boolean
    )

    /**
     * Starts listening for responses from the server to requests that were
     * sent using [loadActualStockPriceWithLimiter]
     *
     * @return [BaseResponse] object with [ActualStockPriceResponse] data as flow
     * */
    fun observeActualStockPriceResponses(): Flow<BaseResponse<ActualStockPriceResponse>>
}