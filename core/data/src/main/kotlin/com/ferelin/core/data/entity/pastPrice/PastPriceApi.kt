package com.ferelin.core.data.entity.pastPrice

import com.ferelin.core.ONE_YEAR_MILLIS
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query

internal interface PastPricesApi {
  @GET("stock/candle")
  fun load(
    @Query("symbol") companyTicker: String,
    @Query("from") from: Long = PastPricesApiSettings.yearAgoMillis,
    @Query("to") to: Long = PastPricesApiSettings.currentMillis,
    @Query("resolution") resolution: String = PastPricesApiSettings.resolution
  ): PastPricesResponse
}

@JsonClass(generateAdapter = true)
internal data class PastPricesResponse(
  @Json(name = "c") val closePrices: List<Double>,
  @Json(name = "t") val timestamps: List<Long>,
)

internal object PastPricesApiSettings {
  const val resolution = "D"

  val currentMillis: Long
    get() = convertForRequest(System.currentTimeMillis())

  val yearAgoMillis: Long
    get() = convertForRequest(System.currentTimeMillis() - ONE_YEAR_MILLIS)

  private fun convertForRequest(value: Long): Long {
    val str = value.toString()
    return when {
      str.length < 3 -> value
      else -> str.substring(0, str.length - 3).toLong()
    }
  }
}