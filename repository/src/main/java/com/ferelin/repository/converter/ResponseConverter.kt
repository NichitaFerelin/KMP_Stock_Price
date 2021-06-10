package com.ferelin.repository.converter

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

import com.ferelin.local.models.Company
import com.ferelin.local.responses.CompaniesResponse
import com.ferelin.local.responses.SearchesResponse
import com.ferelin.remote.api.companyNews.CompanyNewsResponse
import com.ferelin.remote.api.companyProfile.CompanyProfileResponse
import com.ferelin.remote.api.companyQuote.CompanyQuoteResponse
import com.ferelin.remote.api.stockCandles.StockCandlesResponse
import com.ferelin.remote.api.stockSymbols.StockSymbolResponse
import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.webSocket.response.WebSocketResponse
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.repository.utils.RepositoryResponse

interface ResponseConverter {

    fun convertCompaniesResponse(response: CompaniesResponse): RepositoryResponse<List<AdaptiveCompany>>

    fun convertWebSocketResponse(response: BaseResponse<WebSocketResponse>): RepositoryResponse<AdaptiveWebSocketPrice>

    fun convertStockCandlesResponse(
        response: BaseResponse<StockCandlesResponse>,
        symbol: String
    ): RepositoryResponse<AdaptiveCompanyHistory>

    fun convertCompanyProfileResponse(
        response: BaseResponse<CompanyProfileResponse>,
        symbol: String
    ): RepositoryResponse<AdaptiveCompanyProfile>

    fun convertStockSymbolsResponse(response: BaseResponse<StockSymbolResponse>): RepositoryResponse<AdaptiveStocksSymbols>

    fun convertCompanyNewsResponse(
        response: BaseResponse<List<CompanyNewsResponse>>,
        symbol: String
    ): RepositoryResponse<AdaptiveCompanyNews>

    fun convertCompanyQuoteResponse(response: BaseResponse<CompanyQuoteResponse>): RepositoryResponse<AdaptiveCompanyDayData>

    fun convertSearchesForResponse(response: SearchesResponse): RepositoryResponse<List<AdaptiveSearchRequest>>

    fun convertCompaniesForInsert(companies: List<AdaptiveCompany>): List<Company>

    fun convertCompanyForInsert(company: AdaptiveCompany): Company

    fun convertSearchesForInsert(search: List<AdaptiveSearchRequest>): Set<String>

    fun convertFirstTimeLaunchStateToResponse(state: Boolean?): RepositoryResponse<Boolean>

    /**
     * @param response is a response from remote module
     * @return [RepositoryResponse] object with [RepositoryMessages]
     */
    fun convertAuthenticationResponse(response: BaseResponse<Boolean>): RepositoryResponse<RepositoryMessages>

    /**
     * @param response is a response from remote module
     * @return [RepositoryResponse] object with string-data
     */
    fun convertRealtimeDatabaseResponse(response: BaseResponse<String?>): RepositoryResponse<String>
}