package com.ferelin.repository.adaptiveModels

data class AdaptiveStockCandle(
    val openPrices: List<String>,
    val highPrices: List<String>,
    val lowPrices: List<String>,
    val closePrices: List<String>,
    val timestamps: List<String>,
    val dayProfit: List<String>
)