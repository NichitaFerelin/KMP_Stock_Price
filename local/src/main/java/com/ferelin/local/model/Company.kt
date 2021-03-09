package com.ferelin.local.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ferelin.local.database.CompaniesDatabase
import com.squareup.moshi.Json

@Entity(tableName = CompaniesDatabase.DB_NAME)
data class Company(
    @PrimaryKey
    @Json(name = "name") val name: String,
    @Json(name = "symbol") val symbol: String,
    @Json(name = "ticker") val ticker: String,
    @Json(name = "logo") val logoUrl: String,
    @Json(name = "country") val country: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "weburl") val webUrl: String,
    @Json(name = "finnhubIndustry") val industry: String,
    @Json(name = "currency") val currency: String,
    @Json(name = "marketCapitalization") val capitalization: String,
    var isFavourite: Boolean = false,
    var dayCurrentPrice: String = "",
    var dayPreviousClosePrice: String = "",
    var dayOpenPrice: String = "",
    var dayHighPrice: String = "",
    var dayLowPrice: String = "",
    var dayProfit: String = "",
    var historyOpenPrices: List<String> = emptyList(),
    var historyHighPrices: List<String> = emptyList(),
    var historyLowPrices: List<String> = emptyList(),
    var historyClosePrices: List<String> = emptyList(),
    var historyTimestampsPrices: List<String> = emptyList(),
    var newsTimestamps: List<String> = emptyList(),
    var newsHeadline: List<String> = emptyList(),
    var newsIds: List<String> = emptyList(),
    var newsImages: List<String> = emptyList(),
    var newsSource: List<String> = emptyList(),
    var newsSummary: List<String> = emptyList(),
    var newsUrl: List<String> = emptyList(),
)
