package com.ferelin.features.about.news

import androidx.compose.runtime.Immutable
import com.ferelin.core.domain.entity.CompanyNews
import com.ferelin.core.domain.entity.CompanyNewsId
import com.ferelin.core.ui.viewData.utils.toDateStr

@Immutable
internal class NewsViewData(
    val id: CompanyNewsId,
    val headline: String,
    val source: String,
    val sourceUrl: String,
    val summary: String,
    val date: String
)

internal fun CompanyNews.toNewsViewData(): NewsViewData {
    return NewsViewData(
        id = id,
        headline = this.headline,
        source = this.source,
        sourceUrl = this.sourceUrl,
        summary = this.summary,
        date = this.dateMillis.toDateStr()
    )
}