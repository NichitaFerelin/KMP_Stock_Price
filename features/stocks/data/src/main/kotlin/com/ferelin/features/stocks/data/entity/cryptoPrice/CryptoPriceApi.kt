package com.ferelin.features.stocks.data.entity.cryptoPrice

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import retrofit2.http.GET
import retrofit2.http.Query

internal interface CryptoPriceApi {
  @GET("currencies/ticker")
  fun load(
    @Query("ids") cryptoTickers: String,
  ): CryptoPriceResponse
}

@JsonClass(generateAdapter = true)
internal data class CryptoPriceResponse(
  val data: List<CryptoPricePojo>
)

@JsonClass(generateAdapter = true)
internal data class CryptoPricePojo(
  @Json(name = "symbol") val ticker: String,
  @Json(name = "price") val price: String,
  @Json(name = "price_timestamp") val priceTimestamp: String,
  @Json(name = "7d") val priceChangeInfo: PriceChangeInfo
)

@JsonClass(generateAdapter = true)
internal data class PriceChangeInfo(
  @Json(name = "price_change") val value: String,
  @Json(name = "price_change_pct") val percents: String
)