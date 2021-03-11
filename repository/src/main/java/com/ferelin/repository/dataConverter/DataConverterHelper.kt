package com.ferelin.repository.dataConverter

import com.ferelin.local.models.Company
import com.ferelin.local.responses.CompaniesResponse
import com.ferelin.local.responses.SearchesResponse
import com.ferelin.remote.base.BaseResponse
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utils.RepositoryResponse

interface DataConverterHelper {

    fun convertCompaniesResponse(response: CompaniesResponse): RepositoryResponse<List<AdaptiveCompany>>

    fun convertWebSocketResponse(response: BaseResponse): RepositoryResponse<AdaptiveWebSocketPrice>

    fun convertStockCandlesResponse(
        response: BaseResponse,
        symbol: String
    ): RepositoryResponse<AdaptiveCompanyHistory>

    fun convertCompanyProfileResponse(
        response: BaseResponse,
        symbol: String
    ): RepositoryResponse<AdaptiveCompanyProfile>

    fun convertStockSymbolsResponse(response: BaseResponse): RepositoryResponse<AdaptiveStocksSymbols>

    fun convertCompanyNewsResponse(
        response: BaseResponse,
        symbol: String
    ): RepositoryResponse<AdaptiveCompanyNews>

    fun convertCompanyQuoteResponse(response: BaseResponse): RepositoryResponse<AdaptiveCompanyDayData>

    fun convertSearchesForResponse(response: SearchesResponse): RepositoryResponse<List<AdaptiveSearchRequest>>

    fun convertCompaniesForInsert(companies: List<AdaptiveCompany>): List<Company>

    fun convertCompanyForInsert(company: AdaptiveCompany): Company

    fun convertSearchesForInsert(search: List<AdaptiveSearchRequest>): Set<String>
}