package com.ferelin.stockprice.shared.ui.mapper

import com.ferelin.stockprice.shared.domain.entity.News
import com.ferelin.stockprice.shared.ui.toDateStr
import com.ferelin.stockprice.shared.ui.viewData.NewsViewData

object NewsMapper {
  fun map(news: News): NewsViewData {
    return NewsViewData(
      id = news.id,
      headline = news.headline,
      date = news.dateMillis.toDateStr(),
      source = news.source,
      sourceUrl = news.sourceUrl,
      summary = news.summary
    )
  }
}