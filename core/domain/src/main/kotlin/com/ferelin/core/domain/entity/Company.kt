package com.ferelin.core.domain.entity

data class Company(
    val id: CompanyId,
    val name: String,
    val ticker: String,
    val logoUrl: String,
    val country: String,
    val industry: String,
    val phone: String,
    val webUrl: String,
    val capitalization: String,
    val isFavourite: Boolean
)

@JvmInline
value class CompanyId(val value: Int)