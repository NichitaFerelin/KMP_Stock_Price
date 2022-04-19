package com.ferelin.core.domain.entity

data class MarketNews(
    val id: MarketNewsId,
    val category: String,
    val headline: String,
    val summary: String,
    val url: String,
    val imageUrl: String,
    val dateMillis: Long
)

@JvmInline
value class MarketNewsId(val value: Long)