package com.ferelin.remote.webSocket

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.utilits.Api
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class WebResponseConverter {

    private val mConverter = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val mListAdapter = mConverter.adapter(WebSocketSubResponse::class.java)
    private val mResponseAdapter = mConverter.adapter(WebSocketResponse::class.java).lenient()

    fun fromJson(text: String): BaseResponse = try {
        val webSocketListResponse = mListAdapter.fromJson(text)!!
        val parsedJsonListStr = webSocketListResponse.data.first().toString()
        val webSocketResponse = mResponseAdapter.fromJson(parsedJsonListStr)!!

        when {
            webSocketResponse.symbol.isEmpty() -> BaseResponse(Api.RESPONSE_NO_DATA)
            webSocketResponse.volume == 0.0 -> BaseResponse(Api.RESPONSE_TRADE_NOT_AVAILABLE)
            else -> webSocketResponse.apply { responseCode = Api.RESPONSE_OK }
        }
    } catch (e: Exception) {
        BaseResponse(Api.RESPONSE_UNDEFINED)
    }
}