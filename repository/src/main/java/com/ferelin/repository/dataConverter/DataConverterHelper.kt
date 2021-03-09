package com.ferelin.repository.dataConverter

import com.ferelin.local.model.CompaniesResponse
import com.ferelin.local.model.Company
import com.ferelin.local.model.PreferencesResponse
import com.ferelin.local.model.SearchRequest
import com.ferelin.remote.base.BaseResponse
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utilits.RepositoryResponse

interface DataConverterHelper {

    fun convertDatabaseCompanies(
        response: CompaniesResponse,
        onNewData: (companies: List<AdaptiveCompany>) -> Unit
    ): RepositoryResponse<List<AdaptiveCompany>>

    fun convertWebSocketResponse(response: BaseResponse): RepositoryResponse<AdaptiveLastPrice>

    fun convertStockCandleResponse(
        response: BaseResponse
    ): RepositoryResponse<AdaptiveStockCandles>

    fun convertCompanyProfileResponse(
        response: BaseResponse,
        symbol: String,
        onNewData: (Company) -> Unit
    ): RepositoryResponse<AdaptiveCompanyProfile>

    fun convertStockSymbolsResponse(response: BaseResponse): RepositoryResponse<AdaptiveStockSymbols>

    fun convertCompanyNewsResponse(
        response: BaseResponse,
        symbol: String
    ): RepositoryResponse<AdaptiveCompanyNews>

    fun convertCompanyQuoteResponse(response: BaseResponse): RepositoryResponse<AdaptiveCompanyQuote>

    fun convertSearchesForResponse(response: PreferencesResponse): RepositoryResponse<List<AdaptiveSearchRequest>>

    fun convertCompaniesForInsert(companies: List<AdaptiveCompany>): List<Company>

    fun convertCompanyForInsert(company: AdaptiveCompany): Company

    fun convertSearchForInsert(search: AdaptiveSearchRequest): SearchRequest
}