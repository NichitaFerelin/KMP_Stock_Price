package com.ferelin.remote.webSocket

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.utilits.Api
import kotlinx.coroutines.flow.Flow

interface WebSocketConnectorHelper {
    fun openConnection(dataToSubscribe: Collection<String>, token: String = Api.FINNHUB_TOKEN): Flow<BaseResponse>
}