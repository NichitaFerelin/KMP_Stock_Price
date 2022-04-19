package com.ferelin.core.domain.entity

data class CompanyNews(
    val id: CompanyNewsId,
    val companyId: CompanyId,
    val headline: String,
    val source: String,
    val sourceUrl: String,
    val summary: String,
    val dateMillis: Long
)

@JvmInline
value class CompanyNewsId(val value: Long)