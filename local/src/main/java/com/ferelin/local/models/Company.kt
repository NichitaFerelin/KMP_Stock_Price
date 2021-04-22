package com.ferelin.local.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ferelin.local.database.CompaniesDatabase
import com.squareup.moshi.Json

@Entity(tableName = CompaniesDatabase.DB_NAME)
data class Company(
    @PrimaryKey
    var id: Int = 0,

    @Json(name = "name") val name: String,
    @Json(name = "symbol") val symbol: String,
    @Json(name = "logo") val logoUrl: String,
    @Json(name = "country") val country: String,
    @Json(name = "phone") val phone: String,
    @Json(name = "weburl") val webUrl: String,
    @Json(name = "finnhubIndustry") val industry: String,
    @Json(name = "currency") val currency: String,
    @Json(name = "marketCapitalization") val capitalization: String,

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
    var historyDatePrices: List<String> = emptyList(),

    var newsDates: List<String> = emptyList(),
    var newsHeadlines: List<String> = emptyList(),
    var newsIds: List<String> = emptyList(),
    var newsPreviewImagesUrls: List<String> = emptyList(),
    var newsSources: List<String> = emptyList(),
    var newsSummaries: List<String> = emptyList(),
    var newsUrls: List<String> = emptyList(),

    var isFavourite: Boolean = false,
    var favouriteOrderIndex: Int = 0
)