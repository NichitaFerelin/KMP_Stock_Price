package com.ferelin.remote.webSocket

import com.ferelin.remote.base.BaseResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import okhttp3.OkHttpClient
import okhttp3.Request

class WebSocketConnector : WebSocketConnectorHelper {

    private val mBase = "wss://ws.finnhub.io?token="
    private val mConverter = WebResponseConverter()

    override fun openConnection(
        dataToSubscribe: Collection<String>,
        token: String
    ): Flow<BaseResponse> = callbackFlow {
        val request = Request.Builder().url("$mBase$token").build()
        OkHttpClient().apply {
            newWebSocket(request, WebSocketManager(dataToSubscribe) {
                offer(mConverter.fromJson(it))
            })
            dispatcher.executorService.shutdown()
        }
        awaitClose()
    }.flowOn(Dispatchers.IO).buffer(onBufferOverflow = BufferOverflow.SUSPEND)
}