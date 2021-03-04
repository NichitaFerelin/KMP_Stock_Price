package com.ferelin.remote.webSocket

import com.ferelin.remote.base.BaseResponse

// Response model
class WebSocketResponse(
    val symbol: String,
    val lastPrice: Double,
    val volume: Double
) : BaseResponse()