package com.ferelin.remote.webSocket

import com.ferelin.remote.base.BaseResponse
import com.squareup.moshi.Json

// Directly for json parsing
data class WebSocketSubResponse(
    @Json(name = "data") val data: List<Any>
) : BaseResponse()