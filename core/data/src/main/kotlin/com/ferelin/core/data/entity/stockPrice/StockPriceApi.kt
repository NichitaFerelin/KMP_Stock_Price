package com.ferelin.core.data.entity.stockPrice

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query

internal interface StockPriceApi {
  @GET("quote")
  fun load(
    @Query("symbol") companyTicker: String,
  ): StockPriceResponse
}

@JsonClass(generateAdapter = true)
internal data class StockPriceResponse(
  @Json(name = "o") val openPrice: Double,
  @Json(name = "h") val highPrice: Double,
  @Json(name = "l") val lowPrice: Double,
  @Json(name = "c") val currentPrice: Double,
  @Json(name = "pc") val previousClosePrice: Double
)