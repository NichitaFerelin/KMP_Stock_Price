package com.ferelin

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.network.companyNews.CompanyNewsResponse
import com.ferelin.remote.network.companyProfile.CompanyProfileResponse
import com.ferelin.remote.network.companyQuote.CompanyQuoteResponse
import com.ferelin.remote.network.stockCandles.StockCandlesResponse
import com.ferelin.remote.network.stockSymbols.StockSymbolResponse
import com.ferelin.remote.utilits.Api
import com.ferelin.remote.webSocket.WebSocketResponse
import com.ferelin.remote.webSocket.WebSocketSubResponse

object FakeRemoteResponses {
    val companyNewsResponse = CompanyNewsResponse(
        dateTime = 1616657358.0,
        headline = "Headline",
        newsId = 104.0,
        previewImageUrl = "Preview",
        newsSource = "Source",
        newsSummary = "Summary",
        newsBrowserUrl = "BrowserURL"
    )
    val baseCompanyNewsResponse = BaseResponse(
        responseCode = Api.RESPONSE_OK,
        responseData = companyNewsResponse
    )

    val companyProfileResponse = CompanyProfileResponse(
        name = "Apple",
        logoUrl = "https://finnhub.io/api/logo?symbol=AAPL",
        country = "US",
        phone = "14089961010",
        webUrl = "https://www.apple.com/",
        industry = "Technology",
        currency = "USD",
        capitalization = 2272605.0
    )
    val baseCompanyProfileResponse = BaseResponse(
        responseCode = Api.RESPONSE_OK,
        responseData = companyProfileResponse
    )

    val companyQuoteResponse = CompanyQuoteResponse(
        openPrice = 100.0,
        highPrice = 200.0,
        lowPrice = 300.0,
        currentPrice = 400.0,
        previousClosePrice = 500.0
    )
    val baseCompanyQuoteResponse = BaseResponse(
        responseCode = Api.RESPONSE_OK,
        responseData = companyQuoteResponse
    )

    val stockCandlesResponse = StockCandlesResponse(
        openPrices = listOf(104.0, 105.0),
        highPrices = listOf(107.9, 108.02),
        lowPrices = listOf(200.0, 1002.3),
        closePrices = listOf(40.0, 312.3),
        volumeData = listOf(102.0, 102.3),
        timestamps = listOf(10000L, 20000L),
        responseStatus = "OK"
    )
    val baseStockCandlesResponse = BaseResponse(
        responseCode = Api.RESPONSE_OK,
        responseData = stockCandlesResponse
    )

    val stocksSymbolsResponse = StockSymbolResponse(listOf("AAPL", "MSFT"))

    val webSocketResponse = WebSocketResponse(
        symbol = "AAPL",
        lastPrice = 100.0,
        volume = 104.0
    )
    val webSocketSubResponse = WebSocketSubResponse(
        data = listOf(
            "\"s\":${webSocketResponse.symbol}",
            "\"p\":${webSocketResponse.lastPrice}",
            "\"v\":${webSocketResponse.volume}"
        )
    )
    val webSocketSuccessStr =
        "{\"data\":[{\"c\":[\"1\",\"8\",\"24\",\"12\"],\"p\":639.47,\"s\":\"AAPL\",\"t\":1616659809881,\"v\":1}],\"type\":\"trade\"}"
    val webSocketNoVolumeStr =
        "{\"data\":[{\"c\":[\"1\",\"8\",\"24\",\"12\"],\"p\":639.47,\"s\":\"AAPL\",\"t\":1616659809881,\"v\":0.0}],\"type\":\"trade\"}"
    val webSocketUndefinedStr = "{\"data\":1}],\"type\":\"trade\"}"
    val wabSocketOpenPriceHolder =
        hashMapOf(webSocketResponse.symbol to webSocketResponse.lastPrice)
    val baseWebSocketResponse = BaseResponse(
        responseCode = Api.RESPONSE_OK,
        responseData = webSocketResponse
    )
}