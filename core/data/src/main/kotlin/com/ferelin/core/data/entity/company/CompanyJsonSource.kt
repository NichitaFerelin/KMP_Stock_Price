package com.ferelin.core.data.entity.company

import android.content.Context
import com.ferelin.core.data.mapper.CompanyMapper
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import stockprice.CompanyDBO
import stockprice.ProfileDBO

internal interface CompanyJsonSource {
  fun parseJson(): List<Pair<CompanyDBO, ProfileDBO>>
}

internal class CompanyJsonSourceImpl(
  private val context: Context,
  private val moshi: Moshi
) : CompanyJsonSource {
  override fun parseJson(): List<Pair<CompanyDBO, ProfileDBO>> {
    val type = Types.newParameterizedType(List::class.java, CompanyJson::class.java)
    val json = context.assets
      .open(COMPANIES_JSON_FILE)
      .bufferedReader()
      .use { it.readText() }

    val adapter = moshi.adapter<List<CompanyJson>?>(type)
    val parsedItems = adapter.fromJson(json)!!
    return CompanyMapper.map(parsedItems)
  }
}

internal data class CompanyJson(
  @Json(name = "name") val name: String,
  @Json(name = "symbol") val symbol: String,
  @Json(name = "logo") val logo: String,
  @Json(name = "country") val country: String,
  @Json(name = "phone") val phone: String,
  @Json(name = "weburl") val webUrl: String,
  @Json(name = "finnhubIndustry") val industry: String,
  @Json(name = "currency") val currency: String,
  @Json(name = "marketCapitalization") val capitalization: String
)

internal const val COMPANIES_JSON_FILE = "companies.json"