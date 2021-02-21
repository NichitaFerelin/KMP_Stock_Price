package com.ferelin.repository.tools.remote

import com.ferelin.local.model.Company
import com.ferelin.remote.network.companyProfile.CompanyProfileResponse
import com.ferelin.remote.network.stockCandle.StockCandleResponse
import com.ferelin.remote.webSocket.WebSocketResponse
import com.ferelin.repository.utilits.Response
import com.ferelin.repository.utilits.TimeMillis

class ResponsesConfigurator : ResponsesConfiguratorHelper {

    override fun configure(response: WebSocketResponse): Response<HashMap<String, Any>> {
        return Response.Success(
            hashMapOf(
                KEY_SYMBOL to response.symbol,
                KEY_LAST_PRICE to response.lastPrice,
                KEY_VOLUME to response.volume
            )
        )
    }

    override fun configure(
        symbol: String,
        response: StockCandleResponse
    ): Response<HashMap<String, Any>> {
        return Response.Success(
            hashMapOf(
                KEY_SYMBOL to symbol,
                KEY_OPEN_PRICES to response.openPrices,
                KEY_HIGH_PRICES to response.highPrices,
                KEY_LOW_PRICES to response.lowPrices,
                KEY_CLOSE_PRICES to response.closePrices,
                KEY_VOLUME_DATA to response.volumeData,
                KEY_TIMESTAMPS to response.timestamps.map {
                    TimeMillis.convertFromResponse(it)
                }
            )
        )
    }

    override fun configure(symbol: String, response: CompanyProfileResponse): Response<Company> {
        return Response.Success(
            Company(
                response.name,
                symbol,
                response.ticker,
                response.logoUrl,
                response.country,
                response.phone,
                response.webUrl,
                response.industry,
                response.currency,
                response.capitalization
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
    }
}