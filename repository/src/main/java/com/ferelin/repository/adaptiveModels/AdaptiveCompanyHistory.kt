package com.ferelin.repository.adaptiveModels

class AdaptiveCompanyHistory(
    var openPrices: List<String>,
    var highPrices: List<String>,
    var lowPrices: List<String>,
    var closePrices: List<String>,
    var datePrices: List<String>
) {
    override fun equals(other: Any?): Boolean {
        return if (other is AdaptiveCompanyHistory) {
            datePrices.firstOrNull() == other.datePrices.firstOrNull()
        } else false
    }

    override fun hashCode(): Int {
        var result = openPrices.hashCode()
        result = 31 * result + highPrices.hashCode()
        result = 31 * result + lowPrices.hashCode()
        result = 31 * result + closePrices.hashCode()
        result = 31 * result + datePrices.hashCode()
        return result
    }
}