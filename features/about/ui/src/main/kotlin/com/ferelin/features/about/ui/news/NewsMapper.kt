package com.ferelin.features.about.ui.news

import com.ferelin.core.ui.viewData.utils.toDateStr
import com.ferelin.features.about.domain.entities.News

internal object NewsMapper {
  fun map(news: News): NewsViewData {
    return NewsViewData(
      id = news.id,
      headline = news.headline,
      date = news.date.toLong().toDateStr(),
      previewImageUrl = news.previewImageUrl,
      source = news.source,
      sourceUrl = news.sourceUrl,
      summary = news.summary
    )
  }
}