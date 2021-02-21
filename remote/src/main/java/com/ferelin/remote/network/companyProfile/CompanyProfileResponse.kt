package com.ferelin.remote.network.companyProfile

import com.ferelin.remote.base.BaseResponse
import com.squareup.moshi.Json

data class CompanyProfileResponse(
    @Json(name = "name") val name: String,
    @Json(name = "ticker") val ticker: String,
    @Json(name = "logo") val logoUrl: String,
    @Json(name = "country") val country: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "weburl") val webUrl: String,
    @Json(name = "finnhubIndustry") val industry: String,
    @Json(name = "currency") val currency: String,
    @Json(name = "marketCapitalization") val capitalization: Double
) : BaseResponse()
