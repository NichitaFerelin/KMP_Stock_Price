package com.ferelin.remote.webSocket

import com.squareup.moshi.Json

sealed class WebSocketResponse {
    data class Success(
        @Json(name = "s") val symbol: String,
        @Json(name = "p") val lastPrice: Double,
        @Json(name = "v") val volume: Double
    ) : WebSocketResponse()

    class Failed(code: Int) : WebSocketResponse()

    companion object {
        const val SOCKET_CLOSED = 470
        const val TRADE_NOT_AVAILABLE = 471
    }
}