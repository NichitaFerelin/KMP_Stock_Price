package com.ferelin.stockprice.shared.domain.entity

data class Company(
    val id: CompanyId,
    val name: String,
    val ticker: String,
    val logoUrl: String
)

@JvmInline
value class CompanyId(val value: Int)