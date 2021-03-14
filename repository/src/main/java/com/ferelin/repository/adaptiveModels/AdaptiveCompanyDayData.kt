package com.ferelin.repository.adaptiveModels

data class AdaptiveCompanyDayData(
    var currentPrice: String,
    var previousClosePrice: String,
    var openPrice: String,
    var highPrice: String,
    var lowPrice: String,
    var profit: String,
) {
    override fun equals(other: Any?): Boolean {
        return if (other is AdaptiveCompanyDayData) {
            other.currentPrice == currentPrice
        } else false
    }

    override fun hashCode(): Int {
        var result = currentPrice.hashCode()
        result = 31 * result + previousClosePrice.hashCode()
        result = 31 * result + openPrice.hashCode()
        result = 31 * result + highPrice.hashCode()
        result = 31 * result + lowPrice.hashCode()
        result = 31 * result + profit.hashCode()
        return result
    }
}