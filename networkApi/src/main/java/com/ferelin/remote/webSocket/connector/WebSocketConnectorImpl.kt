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

package com.ferelin.remote.webSocket.connector

import com.ferelin.remote.RESPONSE_WEB_SOCKET_CLOSED
import com.ferelin.remote.utils.BaseResponse
import com.ferelin.remote.webSocket.AppWebSocketListener
import com.ferelin.remote.webSocket.WebResponseConverter
import com.ferelin.remote.webSocket.response.WebSocketResponse
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
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
    @Named("FinnhubWebSocketUrl") private val mBaseUrl: String,
    @Named("FinnhubToken") private val mToken: String
) : WebSocketConnector {

    private val mResponseConverter = WebResponseConverter()

    private var mWebSocket: WebSocket? = null

    /*
    * If the web socket has not yet been initialized, then incoming tasks
    * are inserted into this queue.
    * */
    private var mMessagesQueue: Queue<String> = LinkedList()

    override fun subscribe(symbol: String) {
        mWebSocket?.let {
            subscribe(it, symbol)
        } ?: mMessagesQueue.offer(symbol)
    }

    override fun unsubscribe(symbol: String) {
        mWebSocket?.send("{\"type\":\"unsubscribe\",\"symbol\":\"$symbol\"}")
    }

    override fun openConnection(): Flow<BaseResponse<WebSocketResponse>> = callbackFlow {
        val request = Request
            .Builder()
            .url("$mBaseUrl$mToken")
            .build()

        val okHttp = OkHttpClient()

        mWebSocket = okHttp.newWebSocket(
            request = request,
            listener = AppWebSocketListener { response ->
                val converted = mResponseConverter.fromJson(response)
                this.trySend(converted).isSuccess
            })
            .also { webSocket ->
                while (mMessagesQueue.isNotEmpty()) {
                    subscribe(webSocket, mMessagesQueue.poll()!!)
                }
            }
        okHttp.dispatcher.executorService.shutdown()

        awaitClose { mWebSocket?.close(RESPONSE_WEB_SOCKET_CLOSED, null) }
    }
        .buffer(onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override fun closeConnection() {
        mWebSocket?.close(RESPONSE_WEB_SOCKET_CLOSED, null)
        mWebSocket = null
    }

    private fun subscribe(webSocket: WebSocket, symbol: String) {
        webSocket.send("{\"type\":\"subscribe\",\"symbol\":\"$symbol\"}")
    }
}