package com.ferelin.stockprice.shared.data.entity.company

import io.ktor.client.*
import io.ktor.client.request.*
import kotlinx.serialization.SerialName

internal interface CompanyApi {
  suspend fun load(): List<CompanyPojo>
}

internal class CompanyApiImpl(
  private val client: HttpClient
) : CompanyApi {
  override suspend fun load(): List<CompanyPojo> {
    return client.get { url(COMPANY_SOURCE_URL) }
  }
}

@Suppress("PLUGIN_IS_NOT_ENABLED")
@kotlinx.serialization.Serializable
internal data class CompanyPojo(
  @SerialName(value = "name") val name: String,
  @SerialName(value = "symbol") val symbol: String,
  @SerialName(value = "logo") val logo: String,
  @SerialName(value = "country") val country: String,
  @SerialName(value = "phone") val phone: String,
  @SerialName(value = "weburl") val webUrl: String,
  @SerialName(value = "finnhubIndustry") val industry: String,
  @SerialName(value = "currency") val currency: String,
  @SerialName(value = "marketCapitalization") val capitalization: String
)

internal const val COMPANY_SOURCE_URL = "https://api.jsonbin.io/b/624ea2ee936ecb0bbf4608c1"