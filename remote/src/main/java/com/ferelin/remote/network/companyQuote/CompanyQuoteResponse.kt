package com.ferelin.remote.network.companyQuote

import com.ferelin.remote.base.BaseResponse
import com.squareup.moshi.Json

data class CompanyQuoteResponse(
    @Json(name = "o") val openPrice: Double,
    @Json(name = "h") val highPrice: Double,
    @Json(name = "l") val lowPrice: Double,
    @Json(name = "c") val currentPrice: Double,
    @Json(name = "pc") val previousClosePrice: Double
) : BaseResponse()