package com.ferelin.remote.network.stockCandles

import com.squareup.moshi.Json

class StockCandlesResponse(
    @Json(name = "o") val openPrices: List<Double>,
    @Json(name = "h") val highPrices: List<Double>,
    @Json(name = "l") val lowPrices: List<Double>,
    @Json(name = "c") val closePrices: List<Double>,
    @Json(name = "v") val volumeData: List<Double>,
    @Json(name = "t") val timestamps: List<Long>,
    @Json(name = "s") val responseStatus: String
)