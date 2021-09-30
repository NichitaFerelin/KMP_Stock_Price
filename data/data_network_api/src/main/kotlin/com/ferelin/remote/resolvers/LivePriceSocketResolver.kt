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

package com.ferelin.remote.resolvers

import com.ferelin.remote.entities.LivePricePojo
import com.ferelin.remote.utils.AppWebSocketListener
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.WebSocket
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class LivePriceSocketResolver @Inject constructor(
    private val mLivePriceJsonResolver: LivePriceJsonResolver,
    private val mDispatchersProvider: DispatchersProvider,
    @Named("FinnhubWebSocketUrl") private val mBaseUrl: String,
    @Named("FinnhubToken") private val mToken: String
) {
    private var mWebSocket: WebSocket? = null

    /*
    * If the web socket has not yet been initialized, then incoming tasks
    * are inserted into this queue.
    * */
    private var mMessagesQueue: Queue<String> = LinkedList()

    suspend fun subscribe(companyTicker: String) =
        withContext(mDispatchersProvider.IO) {
            mWebSocket?.let {
                subscribe(it, companyTicker)
            } ?: mMessagesQueue.offer(companyTicker)
        }

    suspend fun unsubscribe(companyTicker: String) =
        withContext(mDispatchersProvider.IO) {
            mWebSocket?.send("{\"type\":\"unsubscribe\",\"symbol\":\"$companyTicker\"}")
        }

    fun openConnection(): Flow<LivePricePojo?> = callbackFlow {

        val request = Request
            .Builder()
            .url("$mBaseUrl$mToken")
            .build()

        val okHttp = OkHttpClient()

        mWebSocket = okHttp.newWebSocket(
            request = request,
            listener = AppWebSocketListener { response ->
                val converted = mLivePriceJsonResolver.fromJson(response)
                this.trySend(converted).isSuccess
            })
            .also { webSocket ->
                while (mMessagesQueue.isNotEmpty()) {
                    subscribe(webSocket, mMessagesQueue.poll()!!)
                }
            }
        okHttp.dispatcher.executorService.shutdown()

        awaitClose { closeConnection() }
    }
        .buffer(onBufferOverflow = BufferOverflow.DROP_OLDEST)

    fun closeConnection() {
        mWebSocket?.close(0, null)
        mWebSocket = null
    }

    private fun subscribe(webSocket: WebSocket, symbol: String) {
        webSocket.send("{\"type\":\"subscribe\",\"symbol\":\"$symbol\"}")
    }
}