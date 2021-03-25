package com.ferelin.remote.webSocket

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.utilits.Api
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.*

open class WebSocketConnector : WebSocketConnectorHelper {

    private val mBase = "wss://ws.finnhub.io?token="
    private val mConverter = WebResponseConverter()

    private var mWebSocket: WebSocket? = null
    private var mMessagesQueue: Queue<String> = LinkedList()

    private var mOpenPricesHolder = hashMapOf<String, Double>()

    override fun subscribeItem(symbol: String, openPrice: Double) {
        mOpenPricesHolder[symbol] = openPrice
        mWebSocket?.let {
            subscribe(it, symbol)
        } ?: mMessagesQueue.offer(symbol)
    }

    override fun unsubscribeItem(symbol: String) {
        mWebSocket?.send("{\"type\":\"unsubscribe\",\"symbol\":\"$symbol\"}")
    }

    @FlowPreview
    override fun openConnection(token: String): Flow<BaseResponse<WebSocketResponse>> =
        callbackFlow {
            val request = Request.Builder().url("$mBase$token").build()
            val okHttp = OkHttpClient()
            mWebSocket = okHttp.newWebSocket(request, WebSocketManager {
                val converted = mConverter.fromJson(it, mOpenPricesHolder)
                offer(converted)
            }).also { while (mMessagesQueue.isNotEmpty()) subscribe(it, mMessagesQueue.poll()!!) }
            okHttp.dispatcher.executorService.shutdown()

            awaitClose { mWebSocket?.close(Api.RESPONSE_WEB_SOCKET_CLOSED, null) }
        }.debounce(100).buffer(onBufferOverflow = BufferOverflow.DROP_OLDEST)

    override fun closeConnection() {
        mWebSocket?.close(Api.RESPONSE_WEB_SOCKET_CLOSED, null)
    }

    private fun subscribe(webSocket: WebSocket, symbol: String) {
        webSocket.send("{\"type\":\"subscribe\",\"symbol\":\"$symbol\"}")
    }
}