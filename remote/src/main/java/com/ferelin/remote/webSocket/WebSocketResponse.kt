package com.ferelin.remote.webSocket

import com.squareup.moshi.Json

class WebSocketResponse(
    @Json(name = "s") val symbol: String,
    @Json(name = "p") val lastPrice: Double,
    @Json(name = "v") val volume: Double
)