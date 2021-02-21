package com.ferelin.remote.network.stockCandle

import com.ferelin.remote.base.BaseResponse
import com.squareup.moshi.Json

data class StockCandleResponse(
    @Json(name = "o") val openPrices: List<Double>,
    @Json(name = "h") val highPrices: List<Double>,
    @Json(name = "l") val lowPrices: List<Double>,
    @Json(name = "c") val closePrices: List<Double>,
    @Json(name = "v") val volumeData: List<Double>,
    @Json(name = "t") val timestamps: List<Long>,
    @Json(name = "s") val responseStatus: String
) : BaseResponse()