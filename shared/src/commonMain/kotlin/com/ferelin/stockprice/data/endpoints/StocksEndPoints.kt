package com.ferelin.stockprice.data.endpoints

import com.ferelin.stockprice.data.entity.news.NewsRequestOptions
import com.ferelin.stockprice.data.entity.pastPrice.PastPricesOptions
import com.ferelin.stockprice.data.entity.stockPrice.StockPriceOptions
import io.ktor.client.request.*

internal fun HttpRequestBuilder.news(options: NewsRequestOptions) {
  url(STOCKS_BASE_URL + "company-news")
  parameter("token", options.token)
  parameter("symbol", options.companyTicker)
  parameter("from", options.from)
  parameter("to", options.to)
}

internal fun HttpRequestBuilder.pastPrice(options: PastPricesOptions) {
  url(STOCKS_BASE_URL + "stock/candle")
  parameter("token", options.token)
  parameter("symbol", options.companyTicker)
  parameter("from", options.from)
  parameter("to", options.to)
  parameter("resolution", options.resolution)
}

internal fun HttpRequestBuilder.stockPrice(options: StockPriceOptions) {
  url(STOCKS_BASE_URL + "quote")
  parameter("token", options.token)
  parameter("symbol", options.companyTicker)
}

internal const val STOCKS_BASE_URL = "https://finnhub.io/api/v1/"