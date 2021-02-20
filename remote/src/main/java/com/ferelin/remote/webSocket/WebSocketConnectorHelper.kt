package com.ferelin.remote.webSocket

import kotlinx.coroutines.flow.Flow

interface WebSocketConnectorHelper {
    fun openConnection(dataToSubscribe: Collection<String>,token: String ): Flow<WebSocketResponse>
}