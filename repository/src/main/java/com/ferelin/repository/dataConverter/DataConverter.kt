package com.ferelin.repository.dataConverter

import com.ferelin.local.models.Company
import com.ferelin.local.responses.CompaniesResponse
import com.ferelin.local.responses.Responses
import com.ferelin.local.responses.SearchesResponse
import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.network.companyNews.CompanyNewsResponse
import com.ferelin.remote.network.companyProfile.CompanyProfileResponse
import com.ferelin.remote.network.companyQuote.CompanyQuoteResponse
import com.ferelin.remote.network.stockCandles.StockCandlesResponse
import com.ferelin.remote.network.stockSymbols.StockSymbolResponse
import com.ferelin.remote.utilits.Api
import com.ferelin.remote.webSocket.WebSocketResponse
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.repository.utils.Time

class DataConverter : DataConverterHelper {

    private val mAdapter = DataAdapter()

    override fun convertCompaniesResponse(
        response: CompaniesResponse
    ): RepositoryResponse<List<AdaptiveCompany>> {
        return if (response is CompaniesResponse.Success) {

            val preparedData = when (response.code) {
                Responses.LOADED_FROM_JSON -> response.companies.map {
                    mAdapter.toAdaptiveCompanyFromJson(it)
                }
                else -> response.companies.map { mAdapter.toAdaptiveCompany(it) }
            }
            RepositoryResponse.Success(data = preparedData)
        } else RepositoryResponse.Failed()
    }

    override fun convertWebSocketResponse(response: BaseResponse): RepositoryResponse<AdaptiveWebSocketPrice> {
        return if (response.responseCode == Api.RESPONSE_OK) {
            val itemResponse = response as WebSocketResponse
            val formattedPrice = mAdapter.formatPrice(itemResponse.lastPrice)
            RepositoryResponse.Success(
                owner = itemResponse.symbol,
                data = AdaptiveWebSocketPrice(
                    formattedPrice,
                    mAdapter.calculateProfit(
                        itemResponse.lastPrice,
                        itemResponse.message?.toDouble() ?: 0.0
                    )
                )
            )
        } else RepositoryResponse.Failed(response.responseCode)
    }

    override fun convertStockCandlesResponse(
        response: BaseResponse,
        symbol: String
    ): RepositoryResponse<AdaptiveCompanyHistory> {
        return if (response.responseCode == Api.RESPONSE_OK) {
            val itemResponse = response as StockCandlesResponse
            RepositoryResponse.Success(
                owner = symbol,
                data = AdaptiveCompanyHistory(
                    itemResponse.openPrices.map { mAdapter.formatPrice(it) },
                    itemResponse.highPrices.map { mAdapter.formatPrice(it) },
                    itemResponse.lowPrices.map { mAdapter.formatPrice(it) },
                    itemResponse.closePrices.map { mAdapter.formatPrice(it) },
                    itemResponse.timestamps.map {
                        mAdapter.fromLongToDateStr(Time.convertMillisFromResponse(it))
                    }
                )
            )
        } else RepositoryResponse.Failed(response.responseCode)
    }

    override fun convertCompanyProfileResponse(
        response: BaseResponse,
        symbol: String,
    ): RepositoryResponse<AdaptiveCompanyProfile> {
        return if (response.responseCode == Api.RESPONSE_OK) {
            val itemResponse = response as CompanyProfileResponse
            RepositoryResponse.Success(
                owner = symbol,
                data = AdaptiveCompanyProfile(
                    mAdapter.adaptName(itemResponse.name),
                    symbol,
                    itemResponse.logoUrl,
                    itemResponse.country,
                    mAdapter.adaptPhone(itemResponse.phone),
                    itemResponse.webUrl,
                    itemResponse.industry,
                    itemResponse.currency,
                    mAdapter.formatPrice(itemResponse.capitalization)
                )
            )
        } else RepositoryResponse.Failed(response.responseCode)
    }

    override fun convertStockSymbolsResponse(response: BaseResponse): RepositoryResponse<AdaptiveStocksSymbols> {
        return if (response.responseCode == Api.RESPONSE_OK) {
            val itemResponse = response as StockSymbolResponse
            RepositoryResponse.Success(data = AdaptiveStocksSymbols(itemResponse.stockSymbols))
        } else RepositoryResponse.Failed(response.responseCode)
    }

    override fun convertCompanyNewsResponse(
        response: BaseResponse,
        symbol: String
    ): RepositoryResponse<AdaptiveCompanyNews> {
        return if (response.responseCode == Api.RESPONSE_OK) {
            val itemResponse = response as CompanyNewsResponse
            RepositoryResponse.Success(
                owner = symbol,
                data = AdaptiveCompanyNews(
                    itemResponse.dateTime.map {
                        mAdapter.fromLongToDateStr(mAdapter.convertMillisFromResponse(it.toLong()))
                    },
                    itemResponse.headline,
                    itemResponse.newsId.map { toString().substringBefore(".") },
                    itemResponse.previewImageUrl,
                    itemResponse.newsSource,
                    itemResponse.newsSummary,
                    itemResponse.newsUrl
                )
            )
        } else RepositoryResponse.Failed()
    }

    override fun convertCompanyQuoteResponse(response: BaseResponse): RepositoryResponse<AdaptiveCompanyDayData> {
        return if (response.responseCode == Api.RESPONSE_OK) {
            val itemResponse = response as CompanyQuoteResponse
            RepositoryResponse.Success(
                owner = itemResponse.message,
                data = AdaptiveCompanyDayData(
                    mAdapter.formatPrice(itemResponse.currentPrice),
                    mAdapter.formatPrice(itemResponse.previousClosePrice),
                    mAdapter.formatPrice(itemResponse.openPrice),
                    mAdapter.formatPrice(itemResponse.highPrice),
                    mAdapter.formatPrice(itemResponse.lowPrice),
                    mAdapter.calculateProfit(itemResponse.currentPrice, itemResponse.openPrice)
                )
            )
        } else RepositoryResponse.Failed()
    }

    override fun convertSearchesForResponse(response: SearchesResponse): RepositoryResponse<List<AdaptiveSearchRequest>> {
        return if (response is SearchesResponse.Success) {
            val convertedData = response.data.map { AdaptiveSearchRequest(it) }
            RepositoryResponse.Success(data = convertedData)
        } else RepositoryResponse.Failed()
    }

    override fun convertCompaniesForInsert(companies: List<AdaptiveCompany>): List<Company> {
        return companies.map { convertCompanyForInsert(it) }
    }

    override fun convertCompanyForInsert(company: AdaptiveCompany): Company {
        return mAdapter.toDatabaseCompany(company)
    }

    override fun convertSearchesForInsert(search: List<AdaptiveSearchRequest>): Set<String> {
        val dataSet = mutableSetOf<String>()
        search.forEach { dataSet.add(it.searchText) }
        return dataSet
    }
}