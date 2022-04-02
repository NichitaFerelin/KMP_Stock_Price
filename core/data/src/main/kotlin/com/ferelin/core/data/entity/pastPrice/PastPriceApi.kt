package com.ferelin.core.data.entity.pastPrice

import com.ferelin.core.ONE_YEAR_MILLIS
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query

internal interface PastPricesApi {
  @GET("stock/candle")
  suspend fun load(
    @Query("token") token: String,
    @Query("symbol") companyTicker: String,
    @Query("from") from: Long = PastPricesApiSpecifics.yearAgoMillis,
    @Query("to") to: Long = PastPricesApiSpecifics.currentMillis,
    @Query("resolution") resolution: String = PastPricesApiSpecifics.resolution
  ): PastPricesResponse
}

@JsonClass(generateAdapter = true)
internal data class PastPricesResponse(
  @Json(name = "c") val closePrices: List<Double>,
  @Json(name = "t") val timestamps: List<Long>,
)

internal object PastPricesApiSpecifics {
  const val resolution = "D"

  val currentMillis: Long
    get() = System.currentTimeMillis().toRequestFormat()

  val yearAgoMillis: Long
    get() = (System.currentTimeMillis() - ONE_YEAR_MILLIS).toRequestFormat()

  fun Long.fromRequestFormat(): Long {
    return "${this}000".toLong()
  }

  private fun Long.toRequestFormat(): Long {
    val str = this.toString()
    return when {
      str.length < 3 -> this
      else -> str.substring(0, str.length - 3).toLong()
    }
  }
}