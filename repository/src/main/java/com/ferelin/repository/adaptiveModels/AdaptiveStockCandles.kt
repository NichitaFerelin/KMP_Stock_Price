package com.ferelin.repository.adaptiveModels

data class AdaptiveStockCandles(
    var company: AdaptiveCompany? = null,
    val symbol: String,
    val openPrices: List<String>,
    val highPrices: List<String>,
    val lowPrices: List<String>,
    val closePrices: List<String>,
    val timestamps: List<String>
)