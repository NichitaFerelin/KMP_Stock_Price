package com.ferelin.core.domain.entity

data class News(
  val id: NewsId,
  val companyId: CompanyId,
  val headline: String,
  val source: String,
  val sourceUrl: String,
  val summary: String,
  val date: Double
)

@JvmInline
value class NewsId(val value: String)