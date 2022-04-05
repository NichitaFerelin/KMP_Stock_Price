package com.ferelin.features.about.news

import com.ferelin.stockprice.domain.entity.NewsId

internal data class NewsViewData(
  val id: NewsId,
  val headline: String,
  val source: String,
  val sourceUrl: String,
  val summary: String,
  val date: String
)