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

package com.ferelin.remote.api.webSocket.connector

import com.ferelin.remote.RESPONSE_WEB_SOCKET_CLOSED
import com.ferelin.remote.api.webSocket.WebResponseConverter
import com.ferelin.remote.api.webSocket.WebSocketManager
import com.ferelin.remote.api.webSocket.response.WebSocketResponse
import com.ferelin.remote.base.BaseResponse
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
open class WebSocketConnectorImpl @Inject constructor(
    @Named("FinnhubToken") private val mToken: String
) : WebSocketConnector {

    private val mBaseApiUrl = "wss://ws.finnhub.io?token="
    private val mResponseConverter = WebResponseConverter()

    private var mWebSocket: WebSocket? = null

    /*
    * If the web socket has not yet been initialized,
    * then incoming tasks are inserted into this queue.
    * */
    private var mMessagesQueue: Queue<String> = LinkedList()

    /*
    * Holding open price "owner" symbol with previous price for response
    * */
    private var mOpenPricesHolder = hashMapOf<String, Double>()

    override fun subscribeItemOnLiveTimeUpdates(symbol: String, previousPrice: Double) {
        mOpenPricesHolder[symbol] = previousPrice
        mWebSocket?.let {
            subscribe(it, symbol)
        } ?: mMessagesQueue.offer(symbol)
    }

    override fun unsubscribeItemFromLiveTimeUpdates(symbol: String) {
        mWebSocket?.send("{\"type\":\"unsubscribe\",\"symbol\":\"$symbol\"}")
    }

    override fun openWebSocketConnection(): Flow<BaseResponse<WebSocketResponse>> =
        callbackFlow {
            val request = Request.Builder().url("$mBaseApiUrl$mToken").build()
            val okHttp = OkHttpClient()

            mWebSocket = okHttp.newWebSocket(request, WebSocketManager {
                val converted = mResponseConverter.fromJson(it, mOpenPricesHolder)
                this.trySend(converted).isSuccess
            }).also {
                while (mMessagesQueue.isNotEmpty()) {
                    subscribe(it, mMessagesQueue.poll()!!)
                }
            }
            okHttp.dispatcher.executorService.shutdown()

            awaitClose { mWebSocket?.close(RESPONSE_WEB_SOCKET_CLOSED, null) }
        }.buffer(onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override fun closeWebSocketConnection() {
        mWebSocket?.close(RESPONSE_WEB_SOCKET_CLOSED, null)
        mWebSocket = null
    }

    private fun subscribe(webSocket: WebSocket, symbol: String) {
        webSocket.send("{\"type\":\"subscribe\",\"symbol\":\"$symbol\"}")
    }
}