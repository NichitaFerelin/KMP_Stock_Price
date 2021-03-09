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
    var capitalization: String,
    var dayCurrentPrice: String = "",
    var dayPreviousClosePrice: String = "",
    var dayOpenPrice: String = "",
    var dayHighPrice: String = "",
    var dayLowPrice: String = "",
    var dayProfit: String = "",
    var isFavourite: Boolean = false,
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
    var holderBackground: Int = 0,
    var favouriteIconDrawable: Int = 0,
    var dayProfitBackground: Int = 0
) {
    override fun equals(other: Any?): Boolean {
        return if (other is AdaptiveCompany) {
            symbol == other.symbol
        } else false
    }

    override fun hashCode(): Int {
        return "$name$symbol$ticker$logoUrl$country$phone$webUrl$capitalization".hashCode()
    }
}