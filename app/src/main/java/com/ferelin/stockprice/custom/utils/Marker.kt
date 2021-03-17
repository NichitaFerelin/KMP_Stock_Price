package com.ferelin.stockprice.custom.utils

data class Marker(
    val position: Point = Point(0f, 0f),
    val price: Double,
    val priceStr: String,
    val date: String
)