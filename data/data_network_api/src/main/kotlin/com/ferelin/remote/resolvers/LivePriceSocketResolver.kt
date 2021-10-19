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

import com.ferelin.remote.entities.LivePrice
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
import timber.log.Timber
import java.util.*
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class LivePriceSocketResolver @Inject constructor(
    @Named("FinnhubWebSocketUrl") private val baseUrl: String,
    @Named("FinnhubToken") private val token: String,
    private val livePriceJsonResolver: LivePriceJsonResolver,
    private val dispatchersProvider: DispatchersProvider
) {
    private var webSocket: WebSocket? = null

    /*
    * If the web socket has not yet been initialized, then incoming tasks
    * are inserted into this queue.
    * */
    private var messagesQueue: Queue<String> = LinkedList()

    private companion object {
        // Close code must be in range [1000-4000]
        const val DEFAULT_CLOSE_CODE = 4000
    }

    suspend fun subscribe(companyTicker: String) =
        withContext(dispatchersProvider.IO) {
            Timber.d("subscribe (companyTicker = $companyTicker)")

            webSocket?.let {
                subscribe(it, companyTicker)
            } ?: messagesQueue.offer(companyTicker)
        }

    suspend fun unsubscribe(companyTicker: String) =
        withContext(dispatchersProvider.IO) {
            Timber.d("unsubscribe (companyTicker = $companyTicker)")

            webSocket?.send("{\"type\":\"unsubscribe\",\"symbol\":\"$companyTicker\"}")
        }

    fun openConnection(): Flow<LivePrice?> = callbackFlow {
        Timber.d("open connection")

        val request = Request
            .Builder()
            .url("$baseUrl$token")
            .build()

        val okHttp = OkHttpClient()

        webSocket = okHttp.newWebSocket(
            request = request,
            listener = AppWebSocketListener { response ->
                val converted = livePriceJsonResolver.fromJson(response)
                this.trySend(converted)
            })
            .also { webSocket ->
                // Executes messages from the queue
                while (messagesQueue.isNotEmpty()) {
                    subscribe(webSocket, messagesQueue.poll()!!)
                }
            }
        okHttp.dispatcher.executorService.shutdown()

        awaitClose { closeConnection() }
    }
        .buffer(onBufferOverflow = BufferOverflow.DROP_OLDEST)

    fun closeConnection() {
        Timber.d("close connection")

        webSocket?.close(DEFAULT_CLOSE_CODE, null)
        webSocket = null
    }

    private fun subscribe(webSocket: WebSocket, companyTicker: String) {
        webSocket.send("{\"type\":\"subscribe\",\"symbol\":\"$companyTicker\"}")
    }
}