package com.ferelin.remote.webSocket

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.utils.Api
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

class WebResponseConverter {

    private val mConverter = Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
    private val mListAdapter = mConverter.adapter(WebSocketSubResponse::class.java)
    private val mResponseAdapter = mConverter.adapter(WebSocketResponse::class.java).lenient()

    /**
     * WebSocket Response need to be converted from List to Object.
     * @param [openPricesHolder] used to set owner-stock of price.
     */
    fun fromJson(
        text: String,
        openPricesHolder: HashMap<String, Double>
    ): BaseResponse<WebSocketResponse> = try {
        val webSocketListResponse = mListAdapter.fromJson(text)!!
        val parsedJsonListStr = webSocketListResponse.data.first().toString()
        val responseBody = mResponseAdapter.fromJson(parsedJsonListStr)!!

        BaseResponse(
            responseCode = Api.RESPONSE_OK,
            responseData = responseBody,
            additionalMessage = openPricesHolder[responseBody.symbol].toString()
        )
    } catch (e: Exception) {
        BaseResponse(Api.RESPONSE_UNDEFINED)
    }
}