package com.ferelin.repository.dataConverter

import com.ferelin.local.models.Company
import com.ferelin.local.responses.CompaniesResponse
import com.ferelin.local.responses.SearchesResponse
import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.network.companyNews.CompanyNewsResponse
import com.ferelin.remote.network.companyProfile.CompanyProfileResponse
import com.ferelin.remote.network.companyQuote.CompanyQuoteResponse
import com.ferelin.remote.network.stockCandles.StockCandlesResponse
import com.ferelin.remote.network.stockSymbols.StockSymbolResponse
import com.ferelin.remote.webSocket.WebSocketResponse
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utils.RepositoryResponse

interface DataConverterHelper {

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
}