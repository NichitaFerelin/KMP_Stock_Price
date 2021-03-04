package com.ferelin.remote.webSocket

import android.util.Log
import com.ferelin.remote.utilits.Api
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener

class WebSocketManager(
    private val mDataToSubscribe: Collection<String>,
    private val mOnResponse: (response: String) -> Unit
) : WebSocketListener() {

    override fun onOpen(webSocket: WebSocket, response: Response) {
        super.onOpen(webSocket, response)
        Log.d("Test", "onOpen $response")
        //mDataToSubscribe.forEach {
            webSocket.send("{\"type\":\"subscribe\",\"symbol\":\"AAPL\"}")
        //}
    }

    override fun onMessage(webSocket: WebSocket, text: String) {
        super.onMessage(webSocket, text)
        Log.d("Test", "onMessage: $text")
        mOnResponse(text)
    }

    override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
        super.onFailure(webSocket, t, response)
        Log.d("Test", "on failure: $response")
        Log.d("Test", "$t")
        mOnResponse("")
    }

    override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
        super.onClosing(webSocket, code, reason)
        webSocket.apply {
            close(Api.RESPONSE_WEB_SOCKET_CLOSE, null)
            cancel()
        }
    }
}