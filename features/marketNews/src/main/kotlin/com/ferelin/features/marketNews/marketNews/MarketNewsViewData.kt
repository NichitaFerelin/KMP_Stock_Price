package com.ferelin.features.marketNews.marketNews

import androidx.compose.runtime.Immutable
import com.ferelin.core.domain.entity.MarketNews
import com.ferelin.core.domain.entity.MarketNewsId
import com.ferelin.core.ui.viewData.utils.toDateStr

@Immutable
internal data class MarketNewsViewData(
    val id: MarketNewsId,
    val headline: String,
    val category: String,
    val summary: String,
    val sourceUrl: String,
    val imageUrl: String,
    val date: String
)

internal fun MarketNews.toMarketNewsViewData(): MarketNewsViewData {
    return MarketNewsViewData(
        id = this.id,
        headline = this.headline,
        category = this.category,
        summary = this.summary,
        sourceUrl = this.url,
        imageUrl = this.imageUrl,
        date = this.dateMillis.toDateStr()
    )
}