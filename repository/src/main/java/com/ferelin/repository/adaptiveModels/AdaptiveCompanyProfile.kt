package com.ferelin.repository.adaptiveModels

data class AdaptiveCompanyProfile(
    val name: String,
    val ticker: String,
    val logoUrl: String,
    val country: String,
    val phone: String,
    val webUrl: String,
    val industry: String,
    val currency: String,
    val capitalization: String
)