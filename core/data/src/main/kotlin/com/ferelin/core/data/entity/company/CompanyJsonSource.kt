package com.ferelin.core.data.entity.company

import android.content.Context
import com.ferelin.core.checkBackgroundThread
import com.ferelin.core.data.entity.profile.ProfileDBO
import com.ferelin.core.data.mapper.CompanyMapper
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import javax.inject.Inject

internal interface CompanyJsonSource {
  fun parseJson(): List<Pair<CompanyDBO, ProfileDBO>>
}

internal class CompanyJsonSourceImpl @Inject constructor(
  private val context: Context,
  private val moshi: Moshi
) : CompanyJsonSource {
  override fun parseJson(): List<Pair<CompanyDBO, ProfileDBO>> {
    checkBackgroundThread()
    val type = Types.newParameterizedType(List::class.java, CompanyJson::class.java)
    val json = context.assets
      .open(COMPANY_JSON_FILE)
      .bufferedReader()
      .use { it.readText() }

    val adapter = moshi.adapter<List<CompanyJson>?>(type)
    val parsedItems = adapter.fromJson(json)!!
    return CompanyMapper.map(parsedItems)
  }
}

internal data class CompanyJson(
  val name: String,
  val symbol: String,
  val logo: String,
  val country: String,
  val phone: String,
  val weburl: String,
  val finnhubIndustry: String,
  val currency: String,
  val marketCapitalization: String
)

internal const val COMPANY_JSON_FILE = "companies.json"