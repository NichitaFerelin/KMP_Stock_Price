package com.ferelin.remote.webSocket

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.utilits.Api
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class WebResponseConverter {

    private val mConverter = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val mAdapter = mConverter.adapter(WebSocketSubResponse::class.java)

    // TODO check if throw exception
    fun fromJson(text: String): BaseResponse = try {
        val result = mAdapter.fromJson(text)!!
        val data = result.data
        val lastPrice = data[0] as Double
        val symbol = data[1] as String
        val volume = data.last() as Double
        val response = WebSocketResponse(symbol, lastPrice, volume)

        when {
            text.isEmpty() -> BaseResponse(Api.RESPONSE_NO_DATA)
            volume == 0.0 -> BaseResponse(Api.RESPONSE_TRADE_NOT_AVAILABLE)
            else -> response.apply { responseCode = Api.RESPONSE_OK }
        }
    } catch (e: Exception) {
        BaseResponse(Api.RESPONSE_UNDEFINED)
    }
}