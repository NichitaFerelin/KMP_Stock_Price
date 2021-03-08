package com.ferelin.repository.dataConverter

import com.ferelin.local.model.CompaniesResponse
import com.ferelin.local.model.Company
import com.ferelin.local.model.Search
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
    ): RepositoryResponse<AdaptiveStockCandle>

    fun convertCompanyProfileResponse(
        response: BaseResponse,
        symbol: String,
        onNewData: (Company) -> Unit
    ): RepositoryResponse<AdaptiveCompanyProfile>

    fun convertStockSymbolsResponse(response: BaseResponse): RepositoryResponse<AdaptiveStockSymbols>

    fun convertCompaniesForInsert(companies: List<AdaptiveCompany>): List<Company>

    fun convertCompanyForInsert(company: AdaptiveCompany): Company

    fun convertSearchForResponse(search: Search) : AdaptiveSearch

    fun convertSearchesForResponse(searches: List<Search>): RepositoryResponse<List<AdaptiveSearch>>

    fun convertSearchForInsert(search: AdaptiveSearch): Search
}