package com.ferelin.core.data.api.endPoints

import com.ferelin.core.data.entity.companyNews.CompanyNewsRequestOptions
import com.ferelin.core.data.entity.marketNews.MarketRequestOptions
import com.ferelin.core.data.entity.stockPrice.StockPriceOptions
import io.ktor.client.request.*

internal fun HttpRequestBuilder.news(options: CompanyNewsRequestOptions) {
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

internal fun HttpRequestBuilder.marketNews(options: MarketRequestOptions) {
    url(STOCKS_BASE_URL + "news")
    parameter("token", options.token)
    parameter("category", options.category)
}

internal const val STOCKS_BASE_URL = "https://finnhub.io/api/v1/"