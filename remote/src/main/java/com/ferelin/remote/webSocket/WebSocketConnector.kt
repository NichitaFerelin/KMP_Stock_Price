package com.ferelin.remote.webSocket

import kotlinx.coroutines.flow.Flow

interface WebSocketConnector {
    fun openConnection(): Flow<WebSocketResponse>
}