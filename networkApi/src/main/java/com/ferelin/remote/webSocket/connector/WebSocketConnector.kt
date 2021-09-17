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

import com.ferelin.remote.webSocket.response.WebSocketResponse
import com.ferelin.remote.utils.BaseResponse
import kotlinx.coroutines.flow.Flow

/**
 * Provides methods for interacting with web socket service
 * */
interface WebSocketConnector {

    /**
     * Opens web socket
     *
     * @return live-time price updates as flow
     * */
    fun openConnection(): Flow<BaseResponse<WebSocketResponse>>

    /**
     * Closes web socket connection with server
     * */
    fun closeConnection()

    /**
     * Subscribes new item for live time updates.
     *
     * @param symbol is a company symbol that must be subscribed for updates
     * */
    fun subscribe(symbol: String)

    /**
     * Unsubscribes items from live time updates.
     *
     * @param symbol is a company symbol that must be unsubscribed from updates
     * */
    fun unsubscribe(symbol: String)
}