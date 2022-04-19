package com.ferelin.core.data.entity.marketNews

import com.ferelin.core.data.api.endPoints.marketNews
import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.SerialName

internal interface MarketNewsApi {
    suspend fun load(options: MarketRequestOptions): List<MarketNewsPojo>
}

internal class MarketNewsApiImpl(
    private val client: HttpClient
) : MarketNewsApi {
    override suspend fun load(options: MarketRequestOptions): List<MarketNewsPojo> {
        return client.get { marketNews(options) }.body()
    }
}

internal data class MarketRequestOptions(
    val token: String,
    val category: String = "general"
)

@kotlinx.serialization.Serializable
internal data class MarketNewsPojo(
    @SerialName(value = "id") val id: Long,
    @SerialName(value = "category") val category: String,
    @SerialName(value = "headline") val headline: String,
    @SerialName(value = "summary") val summary: String,
    @SerialName(value = "url") val url: String,
    @SerialName(value = "image") val imageUrl: String,
    @SerialName(value = "datetime") val dateMillis: Long
)