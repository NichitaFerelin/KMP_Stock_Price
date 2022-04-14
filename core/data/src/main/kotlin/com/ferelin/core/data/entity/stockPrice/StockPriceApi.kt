package com.ferelin.core.data.entity.stockPrice

import com.ferelin.core.data.api.endPoints.stockPrice
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.SerialName

internal interface StockPriceApi {
    suspend fun load(options: StockPriceOptions): StockPriceResponse
}

internal class StockPriceApiImpl(
    private val client: HttpClient
) : StockPriceApi {
    override suspend fun load(options: StockPriceOptions): StockPriceResponse {
        return client.get { stockPrice(options) }.body()
    }
}

internal data class StockPriceOptions(
    val token: String,
    val companyTicker: String
)

@kotlinx.serialization.Serializable
internal data class StockPriceResponse(
    @SerialName(value = "o") val openPrice: Double,
    @SerialName(value = "h") val highPrice: Double,
    @SerialName(value = "l") val lowPrice: Double,
    @SerialName(value = "c") val currentPrice: Double,
    @SerialName(value = "pc") val previousClosePrice: Double
)