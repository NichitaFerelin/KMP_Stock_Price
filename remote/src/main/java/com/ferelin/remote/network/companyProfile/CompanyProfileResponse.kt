package com.ferelin.remote.network.companyProfile

import com.squareup.moshi.Json

sealed class CompanyProfileResponse {
    data class Success(
        @field:Json(name = "name") val name: String,
        @field:Json(name = "ticker") val ticker: String,
        @field:Json(name = "logo") val logoUrl: String,
        @field:Json(name = "country") val country: String,
        @field:Json(name = "phone") val phone: String,
        @field:Json(name = "weburl") val webUrl: String,
        @field:Json(name = "finnhubIndustry") val industry: String,
        @field:Json(name = "currency") val currency: String,
        @field:Json(name = "marketCapitalization") val capitalization: Double
    ) : CompanyProfileResponse()

    class Fail(throwable: Throwable) : CompanyProfileResponse()
}