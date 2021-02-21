package com.ferelin.remote.webSocket

import com.ferelin.remote.base.BaseResponse
import kotlinx.coroutines.flow.Flow

interface WebSocketConnectorHelper {
    fun openConnection(dataToSubscribe: Collection<String>, token: String): Flow<BaseResponse>
}