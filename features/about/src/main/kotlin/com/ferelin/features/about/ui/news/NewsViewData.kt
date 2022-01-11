package com.ferelin.features.about.ui.news

import com.ferelin.core.ui.view.adapter.ViewDataType
import com.ferelin.core.domain.entity.NewsId

data class NewsViewData(
  val id: NewsId,
  val headline: String,
  val source: String,
  val sourceUrl: String,
  val summary: String,
  val date: String
) : ViewDataType(NEWS_VIEW_TYPE) {

  override fun getUniqueId(): Long {
    return id.value.hashCode().toLong()
  }
}