package com.ferelin.stockprice.shared.ui.viewData

import com.ferelin.stockprice.shared.domain.entity.NewsId

data class NewsViewData(
    val id: NewsId,
    val headline: String,
    val source: String,
    val sourceUrl: String,
    val summary: String,
    val date: String
)