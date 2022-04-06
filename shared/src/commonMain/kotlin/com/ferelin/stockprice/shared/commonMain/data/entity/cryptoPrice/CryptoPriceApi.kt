@file:Suppress("PLUGIN_IS_NOT_ENABLED")

package com.ferelin.stockprice.shared.commonMain.data.entity.cryptoPrice

import com.ferelin.stockprice.shared.commonMain.data.endpoints.cryptoPrice
import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.SerialName

internal interface CryptoPriceApi {
  suspend fun load(options: CryptoPriceOptions): List<CryptoPricePojo>
}

internal class CryptoPriceApiImpl(
  private val client: HttpClient
) : CryptoPriceApi {
  override suspend fun load(options: CryptoPriceOptions): List<CryptoPricePojo> {
    return client.get { cryptoPrice(options) }
  }
}

internal data class CryptoPriceOptions(
  val token: String,
  val cryptoTickers: String
)

@kotlinx.serialization.Serializable
internal data class CryptoPricePojo(
  @SerialName(value = "symbol") val ticker: String,
  @SerialName(value = "price") val price: String,
  @SerialName(value = "price_timestamp") val priceTimestamp: String,
  @SerialName(value = "7d") val priceChangeInfo: PriceChangeInfo
)

@kotlinx.serialization.Serializable
internal data class PriceChangeInfo(
  @SerialName(value = "price_change") val value: String,
  @SerialName(value = "price_change_pct") val percents: String
)