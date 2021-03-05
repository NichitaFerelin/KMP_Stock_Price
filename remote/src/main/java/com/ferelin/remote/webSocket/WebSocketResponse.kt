package com.ferelin.remote.webSocket

import com.ferelin.remote.base.BaseResponse
import com.squareup.moshi.Json

class WebSocketResponse(
    @Json(name = "s") val symbol: String,
    @Json(name = "p") val lastPrice: Double,
    @Json(name = "v") val volume: Double
) : BaseResponse()