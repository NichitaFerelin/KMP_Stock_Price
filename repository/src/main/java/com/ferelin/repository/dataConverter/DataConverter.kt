package com.ferelin.repository.dataConverter

import com.ferelin.local.model.CompaniesResponse
import com.ferelin.local.model.Company
import com.ferelin.local.model.Responses
import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.network.companyProfile.CompanyProfileResponse
import com.ferelin.remote.network.stockCandles.StockCandlesResponse
import com.ferelin.remote.network.stockSymbols.StockSymbolResponse
import com.ferelin.remote.utilits.Api
import com.ferelin.remote.webSocket.WebSocketResponse
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utilits.RepositoryResponse

class DataConverter : DataConverterHelper {

    private val mAdapter = DataAdapter()

    override fun convertDatabaseCompanies(
        response: CompaniesResponse,
        onNewData: (companies: List<AdaptiveCompany>) -> Unit
    ): RepositoryResponse<List<AdaptiveCompany>> {
        return RepositoryResponse.Success(
            if (response.code == Responses.LOADED_FROM_JSON) {
                val convertedData = response.data.map { mAdapter.toAdaptiveCompanyFromJson(it) }
                onNewData.invoke(convertedData)
                convertedData
            } else response.data.map { mAdapter.toAdaptiveCompany(it) }
        )
    }

    override fun convertWebSocketResponse(response: BaseResponse): RepositoryResponse<AdaptiveLastPrice> {
        return if (response.responseCode == Api.RESPONSE_OK) {
            val itemResponse = response as WebSocketResponse
            RepositoryResponse.Success(
                AdaptiveLastPrice(
                    null, // is delegated to dataInteractor class in app module
                    itemResponse.symbol,
                    mAdapter.adaptPrice(itemResponse.lastPrice)
                )
            )
        } else RepositoryResponse.Failed(response.responseCode)
    }

    override fun convertStockCandleResponse(
        response: BaseResponse
    ): RepositoryResponse<AdaptiveStockCandle> {
        return if (response.responseCode == Api.RESPONSE_OK) {
            val itemResponse = response as StockCandlesResponse
            itemResponse.symbol = response.message!!
            RepositoryResponse.Success(
                AdaptiveStockCandle(
                    null, // is delegated to dataInteractor class in app module
                    response.symbol,
                    itemResponse.openPrices.map { mAdapter.adaptPrice(it) },
                    itemResponse.highPrices.map { mAdapter.adaptPrice(it) },
                    itemResponse.lowPrices.map { mAdapter.adaptPrice(it) },
                    itemResponse.closePrices.map { mAdapter.adaptPrice(it) },
                    itemResponse.timestamps.map { mAdapter.fromLongToDateStr(it) },
                    mAdapter.createDayProfitList(itemResponse.openPrices, itemResponse.closePrices)
                )
            )
        } else RepositoryResponse.Failed(response.responseCode)
    }

    override fun convertCompanyProfileResponse(
        response: BaseResponse,
        symbol: String,
        onNewData: (Company) -> Unit
    ): RepositoryResponse<AdaptiveCompanyProfile> {
        return if (response.responseCode == Api.RESPONSE_OK) {
            val itemResponse = response as CompanyProfileResponse
            val successResponse = RepositoryResponse.Success(
                AdaptiveCompanyProfile(
                    mAdapter.adaptName(itemResponse.name),
                    itemResponse.ticker,
                    itemResponse.logoUrl,
                    itemResponse.country,
                    mAdapter.adaptPhone(itemResponse.phone),
                    itemResponse.webUrl,
                    itemResponse.industry,
                    itemResponse.currency,
                    mAdapter.adaptPrice(itemResponse.capitalization)
                )
            )

            val company = Company(
                successResponse.data.name,
                symbol,
                successResponse.data.ticker,
                successResponse.data.logoUrl,
                successResponse.data.country,
                successResponse.data.phone,
                successResponse.data.webUrl,
                successResponse.data.industry,
                successResponse.data.currency,
                successResponse.data.capitalization
            )
            onNewData.invoke(company)
            successResponse
        } else RepositoryResponse.Failed(response.responseCode)
    }

    override fun convertStockSymbolsResponse(response: BaseResponse): RepositoryResponse<AdaptiveStockSymbols> {
        return if (response.responseCode == Api.RESPONSE_OK) {
            val itemResponse = response as StockSymbolResponse
            RepositoryResponse.Success(
                AdaptiveStockSymbols(itemResponse.stockSymbols)
            )
        } else RepositoryResponse.Failed(response.responseCode)
    }

    override fun convertCompaniesForInsert(companies: List<AdaptiveCompany>): List<Company> {
        return companies.map { convertCompanyForInsert(it) }
    }

    override fun convertCompanyForInsert(company: AdaptiveCompany): Company {
        return mAdapter.toDatabaseCompany(company)
    }
}