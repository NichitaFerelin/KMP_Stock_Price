package com.ferelin.core.data.entity.crypto

import android.content.Context
import com.ferelin.core.data.mapper.CryptoMapper
import kotlinx.serialization.SerialName
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import stockprice.CryptoDBO

internal interface CryptoJsonSource {
    suspend fun parseJson(): List<CryptoDBO>
}

internal class CryptoJsonSourceImpl(
    private val context: Context
) : CryptoJsonSource {
    override suspend fun parseJson(): List<CryptoDBO> {
        val json = context.assets
            .open(CRYPTO_JSON_FILE)
            .bufferedReader()
            .use { it.readText() }
        val cryptosJson = Json.decodeFromString<List<CryptoJson>>(json)
        return CryptoMapper.map(cryptosJson)
    }
}

@kotlinx.serialization.Serializable
internal data class CryptoJson(
    @SerialName(value = "symbol") val symbol: String,
    @SerialName(value = "name") val name: String,
    @SerialName(value = "logo_url") val logoUrl: String
)

internal const val CRYPTO_JSON_FILE = "crypto.json"