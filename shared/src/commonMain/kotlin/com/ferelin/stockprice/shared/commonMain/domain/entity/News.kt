package com.ferelin.stockprice.shared.commonMain.domain.entity

data class News(
  val id: NewsId,
  val companyId: CompanyId,
  val headline: String,
  val source: String,
  val sourceUrl: String,
  val summary: String,
  val dateMillis: Long
)

@JvmInline
value class NewsId(val value: Long)