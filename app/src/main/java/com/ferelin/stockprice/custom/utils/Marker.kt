package com.ferelin.stockprice.custom.utils

data class Marker(
    val position: Point = Point(0f, 0f),
    val price: Double,
    val priceStr: String,
    val date: String
) {
    override fun equals(other: Any?): Boolean {
        return other is Marker && other.date == date && other.price == price
    }

    override fun hashCode(): Int {
        var result = position.hashCode()
        result = 31 * result + price.hashCode()
        result = 31 * result + priceStr.hashCode()
        result = 31 * result + date.hashCode()
        return result
    }
}