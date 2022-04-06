package com.ferelin.stockprice.shared.commonMain.domain.entity

data class SearchRequest(
  val id: SearchId,
  val request: String
)

@JvmInline
value class SearchId(val value: Int)