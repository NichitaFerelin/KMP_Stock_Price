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

package com.ferelin.remote.api

import com.ferelin.remote.api.companyNews.CompanyNewsResponse
import com.ferelin.remote.api.companyProfile.CompanyProfileResponse
import com.ferelin.remote.api.stockHistory.StockHistoryResponse
import com.ferelin.remote.api.stockPrice.StockPriceResponse
import com.ferelin.remote.api.stockSymbols.StockSymbolResponse
import com.ferelin.remote.base.BaseResponse
import kotlinx.coroutines.flow.Flow

interface ApiManager {

    /**
     * Request to load all available stock symbols
     * */
    fun loadStockSymbols(): BaseResponse<StockSymbolResponse>

    /**
     * Request to load company profile
     * @param symbol is a company symbol for which profile need to load
     * */
    fun loadCompanyProfile(symbol: String): BaseResponse<CompanyProfileResponse>

    /**
     * Request to load stock candles(history)
     * @param symbol is a company symbol for which candles need to load
     * @param from is a left timestamp border of data
     * @param to is a right timestamp border of data
     * @param resolution is a type of data model that network must returns(By days, months, years...)
     * */
    fun loadStockHistory(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): BaseResponse<StockHistoryResponse>

    /**
     * Request to load company news
     * @param symbol is a company symbol for which news need to load
     * @param from is a left timestamp border of data
     * @param to is a right timestamp border of data
     * */
    fun loadCompanyNews(
        symbol: String,
        from: String,
        to: String
    ): BaseResponse<List<CompanyNewsResponse>>

    /**
     * Request to load company stock quote.
     * @param symbol is a company symbol for which quote need to load
     * @param position is a position on UI list. Required by ThrottleManagerImpl
     * @param isImportant forces the request to be executed ignoring ThrottleManagerImpl
     * */
    fun sendRequestToLoadPrice(
        symbol: String,
        position: Int,
        isImportant: Boolean
    )

    /**
     * @return flow by which responses to the request will come
     * */
    fun getStockPriceResponseState() : Flow<BaseResponse<StockPriceResponse>>
}