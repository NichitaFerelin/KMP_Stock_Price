package com.ferelin.core.data.entity.cryptoPrice

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import io.reactivex.rxjava3.core.Single
import retrofit2.http.GET
import retrofit2.http.Query

internal interface CryptoPriceApi {
  @GET("currencies/ticker")
  fun load(
    @Query("key") token: String,
    @Query("ids") cryptoTickers: String,
  ): Single<List<CryptoPricePojo>>
}

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