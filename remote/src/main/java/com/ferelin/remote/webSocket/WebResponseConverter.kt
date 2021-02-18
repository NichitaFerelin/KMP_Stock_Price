package com.ferelin.remote.webSocket

import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi

class WebResponseConverter {

    private val mConverter = Moshi.Builder().build()
    private val mAdapter = mConverter.adapter(WebSocketResponse.Success::class.java)

    fun fromJson(text: String): WebSocketResponse = try {
        val response = mAdapter.fromJson(text)
        when {
            response == null -> WebSocketResponse.Failed(0)
            response.volume.toInt() == 0 -> WebSocketResponse.Failed(WebSocketResponse.TRADE_NOT_AVAILABLE)
            else -> response
        }
    } catch (e: JsonDataException) {
        WebSocketResponse.Failed(0)
    }
}