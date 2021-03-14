package com.ferelin.remote.webSocket

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.utilits.Api
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class WebResponseConverter {

    private val mConverter = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val mListAdapter = mConverter.adapter(WebSocketSubResponse::class.java)
    private val mResponseAdapter = mConverter.adapter(WebSocketResponse::class.java).lenient()

    fun fromJson(
        text: String,
        openPricesHolder: HashMap<String, Double>
    ): BaseResponse<WebSocketResponse> = try {
        val webSocketListResponse = mListAdapter.fromJson(text)!!
        val parsedJsonListStr = webSocketListResponse.data.first().toString()
        val responseBody = mResponseAdapter.fromJson(parsedJsonListStr)!!

        val webSocketResponse = BaseResponse(
            responseData = responseBody,
            additionalMessage = openPricesHolder[responseBody.symbol].toString()
        )

        when {
            responseBody.symbol.isEmpty() -> BaseResponse(Api.RESPONSE_NO_DATA)
            responseBody.volume == 0.0 -> BaseResponse(Api.RESPONSE_TRADE_NOT_AVAILABLE)
            else -> webSocketResponse.apply { responseCode = Api.RESPONSE_OK }
        }
    } catch (e: Exception) {
        BaseResponse(Api.RESPONSE_UNDEFINED)
    }
}