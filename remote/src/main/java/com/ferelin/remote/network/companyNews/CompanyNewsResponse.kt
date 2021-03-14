package com.ferelin.remote.network.companyNews

import com.squareup.moshi.Json

class CompanyNewsResponse(
    @Json(name = "datetime") val dateTime: Double,
    @Json(name = "headline") val headline: String,
    @Json(name = "id") val newsId: Double,
    @Json(name = "image") val previewImageUrl: String,
    @Json(name = "source") val newsSource: String,
    @Json(name = "summary") val newsSummary: String,
    @Json(name = "url") val newsBrowserUrl: String
)