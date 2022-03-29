package com.ferelin.features.about.news

import com.ferelin.core.domain.entity.News
import com.ferelin.core.ui.viewData.utils.toDateStr

internal object NewsMapper {
  fun map(news: News): NewsViewData {
    return NewsViewData(
      id = news.id,
      headline = news.headline,
      date = news.date.toDateStr(),
      source = news.source,
      sourceUrl = news.sourceUrl,
      summary = news.summary
    )
  }
}