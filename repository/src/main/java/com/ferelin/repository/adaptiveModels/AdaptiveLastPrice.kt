package com.ferelin.repository.adaptiveModels

data class AdaptiveLastPrice(
    var company: AdaptiveCompany? = null,
    val symbol: String,
    val lastPrice: String
)