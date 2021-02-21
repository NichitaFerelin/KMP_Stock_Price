package com.ferelin.remote.webSocket

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.utilits.Api
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class WebResponseConverter {

    private val mConverter = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val mAdapter = mConverter.adapter(WebSocketResponse::class.java)

    // TODO check if throw exception
    fun fromJson(text: String): BaseResponse {
        val result = mAdapter.fromJson(text)
        return when {
            text.isEmpty() -> BaseResponse(Api.RESPONSE_NO_DATA)
            result == null -> BaseResponse(Api.RESPONSE_UNDEFINED)
            result.volume == 0.0 -> BaseResponse(Api.RESPONSE_TRADE_NOT_AVAILABLE)
            else -> result.apply { code = Api.RESPONSE_OK }
        }
    }
}