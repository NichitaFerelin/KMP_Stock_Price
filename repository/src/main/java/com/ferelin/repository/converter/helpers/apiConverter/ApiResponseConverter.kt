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

package com.ferelin.repository.converter.helpers.apiConverter

import com.ferelin.remote.api.companyNews.CompanyNewsResponse
import com.ferelin.remote.api.companyProfile.CompanyProfileResponse
import com.ferelin.remote.api.companyQuote.CompanyQuoteResponse
import com.ferelin.remote.api.stockCandles.StockCandlesResponse
import com.ferelin.remote.api.stockSymbols.StockSymbolResponse
import com.ferelin.remote.base.BaseResponse
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utils.RepositoryResponse

interface ApiResponseConverter {

    fun convertStockCandlesResponseForUi(
        response: BaseResponse<StockCandlesResponse>,
        symbol: String
    ): RepositoryResponse<AdaptiveCompanyHistory>

    fun convertCompanyProfileResponseForUi(
        response: BaseResponse<CompanyProfileResponse>,
        symbol: String,
    ): RepositoryResponse<AdaptiveCompanyProfile>

    fun convertStockSymbolsResponseForUi(
        response: BaseResponse<StockSymbolResponse>
    ): RepositoryResponse<AdaptiveStocksSymbols>

    fun convertCompanyNewsResponseForUi(
        response: BaseResponse<List<CompanyNewsResponse>>,
        symbol: String
    ): RepositoryResponse<AdaptiveCompanyNews>

    fun convertCompanyQuoteResponseForUi(
        response: BaseResponse<CompanyQuoteResponse>
    ): RepositoryResponse<AdaptiveCompanyDayData>
}