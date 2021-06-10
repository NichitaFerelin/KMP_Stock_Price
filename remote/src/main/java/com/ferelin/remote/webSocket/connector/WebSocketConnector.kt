package com.ferelin.remote.webSocket.connector

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
import kotlinx.coroutines.flow.Flow

interface WebSocketConnector {

    /**
     * Opens web socket and start returns responses on this flow
     * */
    fun openWebSocketConnection(token: String = Api.FINNHUB_TOKEN): Flow<BaseResponse<WebSocketResponse>>

    fun closeWebSocketConnection()

    /**
     * Subscribes new item for live time updates.
     * @param symbol is a company symbol that must be subscribed for updates
     * @param previousPrice is a current price of stock that is used to calculate the price difference
     * */
    fun subscribeItemOnLiveTimeUpdates(symbol: String, previousPrice: Double)

    /**
     * Unsubscribes items from live time updates.
     * @param symbol is a company symbol that must be unsubscribed from updates
     * */
    fun unsubscribeItemFromLiveTimeUpdates(symbol: String)
}