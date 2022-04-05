package com.ferelin.stockprice.data.entity.crypto

import com.ferelin.stockprice.data.mapper.CryptoMapper
import com.ferelin.stockprice.db.CryptoDBO
import kotlinx.serialization.SerialName
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

internal interface CryptoJsonSource {
  fun parseJson(): List<CryptoDBO>
}

internal class CryptoJsonSourceImpl : CryptoJsonSource {
  override fun parseJson(): List<CryptoDBO> {
    val jsonResult = this::class.java.classLoader!!
      .getResourceAsStream(JSON_CRYPTO)!!
      .bufferedReader()
      .use { it.readText() }

    val parsedItems = Json.decodeFromString<List<CryptoJson>>(jsonResult)
    return CryptoMapper.map(parsedItems)
  }
}

@Suppress("PLUGIN_IS_NOT_ENABLED")
@kotlinx.serialization.Serializable
internal data class CryptoJson(
  @SerialName(value = "symbol") val symbol: String,
  @SerialName(value = "name") val name: String,
  @SerialName(value = "logo_url") val logoUrl: String
)

internal const val JSON_CRYPTO = "cryptos.json"