package com.ferelin.features.about.news

import androidx.compose.runtime.Immutable
import com.ferelin.core.domain.entity.News
import com.ferelin.core.domain.entity.NewsId
import com.ferelin.core.ui.viewData.utils.toDateStr

@Immutable
internal class NewsViewData(
    val id: NewsId,
    val headline: String,
    val source: String,
    val sourceUrl: String,
    val summary: String,
    val date: String
)

internal fun News.toNewsViewData(): NewsViewData {
    return NewsViewData(
        id = id,
        headline = this.headline,
        source = this.source,
        sourceUrl = this.sourceUrl,
        summary = this.summary,
        date = this.dateMillis.toDateStr()
    )
}