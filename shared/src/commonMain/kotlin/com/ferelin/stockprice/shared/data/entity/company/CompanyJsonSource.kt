package com.ferelin.stockprice.shared.data.entity.company

import com.ferelin.stockprice.db.CompanyDBO
import com.ferelin.stockprice.db.ProfileDBO
import com.ferelin.stockprice.shared.data.mapper.CompanyMapper
import kotlinx.serialization.SerialName
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

internal interface CompanyJsonSource {
  fun parseJson(): List<Pair<CompanyDBO, ProfileDBO>>
}

internal class CompanyJsonSourceImpl : CompanyJsonSource {
  override fun parseJson(): List<Pair<CompanyDBO, ProfileDBO>> {
    val jsonResult = this::class.java.classLoader!!
      .getResourceAsStream(JSON_COMPANIES)!!
      .bufferedReader()
      .use { it.readText() }

    val parsedItems = Json.decodeFromString<List<CompanyJson>>(jsonResult)
    return CompanyMapper.map(parsedItems)
  }
}

@Suppress("PLUGIN_IS_NOT_ENABLED")
@kotlinx.serialization.Serializable
internal data class CompanyJson(
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

internal const val JSON_COMPANIES = "companies.json"