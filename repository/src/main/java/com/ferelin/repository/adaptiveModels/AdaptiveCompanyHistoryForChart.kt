package com.ferelin.repository.adaptiveModels

class AdaptiveCompanyHistoryForChart(
    val price: List<Double>,
    val priceStr: List<String>,
    val dates: List<String>
) {
    override fun equals(other: Any?): Boolean {
        return if (other is AdaptiveCompanyHistoryForChart) {
            return dates.firstOrNull() == other.dates.firstOrNull()
        } else false
    }

    override fun hashCode(): Int {
        var result = price.hashCode()
        result = 31 * result + priceStr.hashCode()
        result = 31 * result + dates.hashCode()
        return result
    }

    fun isNotEmpty() : Boolean {
        return price.isNotEmpty()
    }

    fun isEmpty() : Boolean {
        return price.isEmpty()
    }
}