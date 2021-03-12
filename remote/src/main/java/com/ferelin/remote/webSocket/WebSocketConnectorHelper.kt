package com.ferelin.remote.webSocket

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.utilits.Api
import kotlinx.coroutines.flow.Flow

interface WebSocketConnectorHelper {

    fun openConnection(token: String = Api.FINNHUB_TOKEN): Flow<BaseResponse>

    fun closeConnection()

    fun subscribeItem(symbol: String, openPrice: Double)

    fun unsubscribeItem(symbol: String)
}