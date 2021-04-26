package com.ferelin.remote.webSocket

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.utils.Api
import kotlinx.coroutines.flow.Flow

interface WebSocketConnectorHelper {

    fun openWebSocketConnection(token: String = Api.FINNHUB_TOKEN): Flow<BaseResponse<WebSocketResponse>>

    fun closeWebSocketConnection()

    fun subscribeItemOnLiveTimeUpdates(symbol: String, previousPrice: Double)

    fun unsubscribeItemFromLiveTimeUpdates(symbol: String)
}