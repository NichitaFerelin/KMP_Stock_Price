package com.ferelin.features.about.domain.entities

import com.ferelin.core.domain.entities.entity.CompanyId

data class News(
  val id: NewsId,
  val companyId: CompanyId,
  val headline: String,
  val previewImageUrl: String,
  val source: String,
  val sourceUrl: String,
  val summary: String,
  val date: Double
)

@JvmInline
value class NewsId(val value: String)