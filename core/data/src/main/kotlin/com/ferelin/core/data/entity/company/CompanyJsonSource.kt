package com.ferelin.core.data.entity.company

import android.content.Context
import com.ferelin.core.data.mapper.CompanyMapper
import kotlinx.serialization.SerialName
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import stockprice.CompanyDBO

internal interface CompanyJsonSource {
    suspend fun parseJson(): List<CompanyDBO>
}

internal class CompanyJsonSourceImpl(
    private val context: Context
) : CompanyJsonSource {
    override suspend fun parseJson(): List<CompanyDBO> {
        val json = context.assets
            .open(COMPANIES_JSON_FILE)
            .bufferedReader()
            .use { it.readText() }
        val companiesJson = Json.decodeFromString<List<CompanyJson>>(json)
        return CompanyMapper.map(companiesJson)
    }
}

@kotlinx.serialization.Serializable
internal data class CompanyJson(
    @SerialName(value = "name") val name: String,
    @SerialName(value = "symbol") val ticker: String,
    @SerialName(value = "logo") val logoUrl: String,
    @SerialName(value = "country") val country: String,
    @SerialName(value = "phone") val phone: String,
    @SerialName(value = "weburl") val webUrl: String,
    @SerialName(value = "finnhubIndustry") val industry: String,
    @SerialName(value = "currency") val currency: String,
    @SerialName(value = "marketCapitalization") val capitalization: String
)

internal const val COMPANIES_JSON_FILE = "companies.json"