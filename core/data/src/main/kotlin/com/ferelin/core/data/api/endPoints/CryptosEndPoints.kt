package com.ferelin.core.data.api.endPoints

import com.ferelin.core.data.entity.cryptoPrice.CryptoPriceOptions
import io.ktor.client.request.*

internal fun HttpRequestBuilder.cryptoPrice(options: CryptoPriceOptions) {
  url(CRYPTOS_BASE_URL + "currencies/ticker")
  parameter("key", options.token)
  parameter("ids", options.cryptoTickers)
}

internal const val CRYPTOS_BASE_URL = "https://api.nomics.com/v1/"