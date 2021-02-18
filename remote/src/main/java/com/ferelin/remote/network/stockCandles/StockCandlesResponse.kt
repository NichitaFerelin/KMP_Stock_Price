package com.ferelin.remote.network.stockCandles

import com.squareup.moshi.Json

sealed class StockCandlesResponse {
    data class Success(
        @field:Json(name = "o") val openPrices: List<Double>,
        @field:Json(name = "h") val highPrices: List<Double>,
        @field:Json(name = "l") val lowPrices: List<Double>,
        @field:Json(name = "c") val closePrices: List<Double>,
        @field:Json(name = "v") val volumeData: List<Double>,
        @field:Json(name = "t") val timestamps: List<Double>,
        @field:Json(name = "s") val responseStatus: String
    ) : StockCandlesResponse()

    class Fail(throwable: Throwable) : StockCandlesResponse()
}