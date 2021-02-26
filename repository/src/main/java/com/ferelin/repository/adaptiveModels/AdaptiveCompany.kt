package com.ferelin.repository.adaptiveModels

data class AdaptiveCompany(
    val name: String,
    val symbol: String,
    val ticker: String,
    val logoUrl: String,
    val country: String,
    val phone: String,
    val webUrl: String,
    val industry: String,
    val currency: String,
    val capitalization: String,
    var lastPrice: String = "0.0",
    var isFavourite: Boolean = false,
    var openPrices: List<String> = emptyList(),
    var highPrices: List<String> = emptyList(),
    var lowPrices: List<String> = emptyList(),
    var closePrices: List<String> = emptyList(),
    var dayProfitPercents: List<String> = emptyList(),
    var timestamps: List<String> = emptyList(),
    var holderBackground: Int = 0,
    var favouriteIconBackground: Int = 0,
    var tickerProfitBackground: Int = 0
)