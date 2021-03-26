package com.ferelin.provider

import com.ferelin.remote.webSocket.WebSocketResponse

object FakeRemoteResponses {
    val webSocketResponse = WebSocketResponse(
        symbol = "AAPL",
        lastPrice = 100.0,
        volume = 104.0
    )
    const val webSocketSuccessStr =
        "{\"data\":[{\"c\":[\"1\",\"8\",\"24\",\"12\"],\"p\":639.47,\"s\":\"AAPL\",\"t\":1616659809881,\"v\":1}],\"type\":\"trade\"}"
    const val webSocketUndefinedStr = "{\"data\":1}],\"type\":\"trade\"}"
    val wabSocketOpenPriceHolder =
        hashMapOf(webSocketResponse.symbol to webSocketResponse.lastPrice)
}