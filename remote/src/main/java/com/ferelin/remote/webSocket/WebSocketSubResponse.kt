package com.ferelin.remote.webSocket

import com.squareup.moshi.Json

class WebSocketSubResponse(
    @Json(name = "data") val data: List<Any>
)