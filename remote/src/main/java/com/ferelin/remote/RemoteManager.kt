package com.ferelin.remote

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.network.NetworkManagerHelper
import com.ferelin.remote.webSocket.WebSocketConnectorHelper
import kotlinx.coroutines.flow.Flow

class RemoteManager(
    private val mNetworkManager: NetworkManagerHelper,
    private val mWebSocketConnector: WebSocketConnectorHelper
) : RemoteManagerHelper {

    override fun openConnection(
        dataToSubscribe: Collection<String>,
        token: String
    ): Flow<BaseResponse> {
        return mWebSocketConnector.openConnection(dataToSubscribe, token)
    }

    override fun loadStockCandle(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): Flow<BaseResponse> {
        return mNetworkManager.loadStockCandle(symbol, from, to, resolution)
    }

    override fun loadCompanyProfile(symbol: String): Flow<BaseResponse> {
        return mNetworkManager.loadCompanyProfile(symbol)
    }

    override fun loadStockSymbols(): Flow<BaseResponse> {
        return mNetworkManager.loadStockSymbols()
    }
}