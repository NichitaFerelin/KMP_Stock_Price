package com.ferelin.remote.network.companyNews

import com.ferelin.remote.base.BaseResponse
import com.squareup.moshi.Json

data class CompanyNewsResponse(
    @Json(name = "datetime") val dateTime: List<Double>,
    @Json(name = "headline") val headline: List<String>,
    @Json(name = "id") val newsId: List<Double>,
    @Json(name = "image") val previewImageUrl: List<String>,
    @Json(name = "source") val newsSource: List<String>,
    @Json(name = "summary") val newsSummary: List<String>,
    @Json(name = "url") val newsUrl: List<String>
) : BaseResponse()