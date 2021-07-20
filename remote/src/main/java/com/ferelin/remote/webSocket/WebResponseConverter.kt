package com.ferelin.remote.webSocket

/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.utils.Api
import com.ferelin.remote.webSocket.response.WebSocketResponse
import com.ferelin.remote.webSocket.response.WebSocketSubResponse
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

/**
 * [WebResponseConverter] can convert response from web-socket for repository module.
 * */
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