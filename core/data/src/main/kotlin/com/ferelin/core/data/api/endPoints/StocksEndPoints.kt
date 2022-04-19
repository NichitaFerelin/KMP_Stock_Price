package com.ferelin.core.data.api.endPoints

import com.ferelin.core.data.entity.news.NewsRequestOptions
import com.ferelin.core.data.entity.stockPrice.StockPriceOptions
import io.ktor.client.request.*

internal fun HttpRequestBuilder.news(options: NewsRequestOptions) {
    url(STOCKS_BASE_URL + "company-news")
    parameter("token", options.token)
    parameter("symbol", options.companyTicker)
    parameter("from", options.from)
    parameter("to", options.to)
}

internal fun HttpRequestBuilder.stockPrice(options: StockPriceOptions) {
    url(STOCKS_BASE_URL + "quote")
    parameter("token", options.token)
    parameter("symbol", options.companyTicker)
}

internal const val STOCKS_BASE_URL = "https://finnhub.io/api/v1/"