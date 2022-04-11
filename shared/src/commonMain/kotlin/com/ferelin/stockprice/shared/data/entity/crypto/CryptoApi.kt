package com.ferelin.stockprice.shared.data.entity.crypto

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.SerialName

internal interface CryptoApi {
    suspend fun load(): List<CryptoPojo>
}

internal class CryptoApiImpl(
    private val client: HttpClient
) : CryptoApi {
    override suspend fun load(): List<CryptoPojo> {
        return client.get { url(CRYPTO_SOURCE_URL) }
    }
}

@Suppress("PLUGIN_IS_NOT_ENABLED")
@kotlinx.serialization.Serializable
internal data class CryptoPojo(
    @SerialName(value = "symbol") val symbol: String,
    @SerialName(value = "name") val name: String,
    @SerialName(value = "logo_url") val logoUrl: String
)

internal const val CRYPTO_SOURCE_URL = "https://api.jsonbin.io/b/624ea2c65912290c00f62b1b/"