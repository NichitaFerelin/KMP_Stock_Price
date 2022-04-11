package com.ferelin.stockprice.shared.domain.entity

data class Profile(
    val id: CompanyId,
    val country: String,
    val phone: String,
    val webUrl: String,
    val industry: String,
    val capitalization: String
)