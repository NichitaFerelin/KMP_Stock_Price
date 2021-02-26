package com.ferelin.repository.tools.remote

import com.ferelin.remote.network.companyProfile.CompanyProfileResponse
import com.ferelin.remote.network.stockCandle.StockCandleResponse
import com.ferelin.remote.webSocket.WebSocketResponse
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.tools.CommonTransformer.calculatePercentDayProfitList
import com.ferelin.repository.tools.CommonTransformer.fromLongToDateStr
import com.ferelin.repository.tools.CommonTransformer.transformCompanyName
import com.ferelin.repository.tools.CommonTransformer.transformPriceStr
import com.ferelin.repository.utilits.Response

class ResponsesConfigurator : ResponsesConfiguratorHelper {

    override fun configure(
        response: WebSocketResponse,
        currency: String
    ): Response<HashMap<String, Any>> {
        return Response.Success(
            hashMapOf(
                KEY_SYMBOL to response.symbol,
                KEY_LAST_PRICE to transformPriceStr(response.lastPrice.toString(), currency),
                KEY_VOLUME to response.volume
            )
        )
    }

    override fun configure(
        symbol: String,
        response: StockCandleResponse
    ): Response<HashMap<String, Any>> {
        val openPrices = response.openPrices.map { transformPriceStr(it.toString(), "USD") }
        val highPrices = response.highPrices.map { transformPriceStr(it.toString(), "USD") }
        val lowPrices = response.lowPrices.map { transformPriceStr(it.toString(), "USD") }
        val closePrices = response.closePrices.map { transformPriceStr(it.toString(), "USD") }
        val timestamps = response.timestamps.map { fromLongToDateStr(it) }
        val percentDayProfit = calculatePercentDayProfitList(response.openPrices, response.closePrices)
        return Response.Success(
            hashMapOf(
                KEY_SYMBOL to symbol,
                KEY_OPEN_PRICES to openPrices,
                KEY_HIGH_PRICES to highPrices,
                KEY_LOW_PRICES to lowPrices,
                KEY_CLOSE_PRICES to closePrices,
                KEY_VOLUME_DATA to response.volumeData,
                KEY_TIMESTAMPS to timestamps,
                KEY_DAY_PROFIT to percentDayProfit
            )
        )
    }

    override fun configure(
        symbol: String,
        response: CompanyProfileResponse
    ): Response<AdaptiveCompany> {
        return Response.Success(
            AdaptiveCompany(
                transformCompanyName(response.name),
                symbol,
                response.ticker,
                response.logoUrl,
                response.country,
                response.phone,
                response.webUrl,
                response.industry,
                response.currency,
                transformPriceStr(response.capitalization.toString(), "USD")
            )
        )
    }

    companion object {
        const val KEY_SYMBOL = "symbol"
        const val KEY_LAST_PRICE = "last price"
        const val KEY_VOLUME = "volume"

        const val KEY_OPEN_PRICES = "open prices"
        const val KEY_HIGH_PRICES = "high prices"
        const val KEY_LOW_PRICES = "low prices"
        const val KEY_CLOSE_PRICES = "close prices"
        const val KEY_VOLUME_DATA = "volume data"
        const val KEY_TIMESTAMPS = "timestamps"
        const val KEY_DAY_PROFIT = "day profit"
    }
}