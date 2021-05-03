package com.ferelin.remote.webSocket

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.utils.Api
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.*

open class WebSocketConnector : WebSocketConnectorHelper {

    private val mBaseApiUrl = "wss://ws.finnhub.io?token="
    private val mResponseConverter = WebResponseConverter()

    private var mWebSocket: WebSocket? = null

    /*
    * If the web socket has not yet been initialized, then incoming tasks are inserted into this queue.
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

    @FlowPreview
    override fun openWebSocketConnection(token: String): Flow<BaseResponse<WebSocketResponse>> =
        callbackFlow {
            val request = Request.Builder().url("$mBaseApiUrl$token").build()
            val okHttp = OkHttpClient()

            mWebSocket = okHttp.newWebSocket(request, WebSocketManager {
                val converted = mResponseConverter.fromJson(it, mOpenPricesHolder)
                offer(converted)
            }).also {
                while (mMessagesQueue.isNotEmpty()) {
                    subscribe(it, mMessagesQueue.poll()!!)
                }
            }
            okHttp.dispatcher.executorService.shutdown()

            awaitClose { mWebSocket?.close(Api.RESPONSE_WEB_SOCKET_CLOSED, null) }
        }
            .debounce(100)
            .buffer(onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override fun closeWebSocketConnection() {
        mWebSocket?.close(Api.RESPONSE_WEB_SOCKET_CLOSED, null)
        mWebSocket = null
    }

    private fun subscribe(webSocket: WebSocket, symbol: String) {
        webSocket.send("{\"type\":\"subscribe\",\"symbol\":\"$symbol\"}")
    }
}