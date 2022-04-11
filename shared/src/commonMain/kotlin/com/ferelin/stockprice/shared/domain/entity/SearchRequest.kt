package com.ferelin.stockprice.shared.domain.entity

data class SearchRequest(
    val id: SearchId,
    val request: String
)

@JvmInline
value class SearchId(val value: Int)