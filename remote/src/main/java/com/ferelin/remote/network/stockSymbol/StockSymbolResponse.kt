package com.ferelin.remote.network.stockSymbol

import com.squareup.moshi.Json

sealed class StockSymbolResponse {
    data class Success(
        @Json(name = "symbol") val symbol: String
    ) : StockSymbolResponse()

    class Fail(throwable: Throwable) : StockSymbolResponse()
}
