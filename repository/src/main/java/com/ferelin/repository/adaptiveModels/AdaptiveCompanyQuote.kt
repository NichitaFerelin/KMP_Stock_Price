package com.ferelin.repository.adaptiveModels

data class AdaptiveCompanyQuote(
    var company: AdaptiveCompany? = null,
    var symbol: String = "",
    val openPrice: String,
    val highPrice: String,
    val lowPrice: String,
    val currentPrice: String,
    val previousClosePrice: String,
    val dayDelta: String
)