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
    var lastPrice: String = "0.0",
    var openPrices: List<String> = emptyList(),
    var highPrices: List<String> = emptyList(),
    var lowPrices: List<String> = emptyList(),
    var closePrices: List<String> = emptyList(),
    var timestamps: List<String> = emptyList()
)
