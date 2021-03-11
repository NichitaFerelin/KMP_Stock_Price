package com.ferelin.remote

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.network.NetworkManagerHelper
import com.ferelin.remote.webSocket.WebSocketConnectorHelper
import kotlinx.coroutines.flow.Flow

class RemoteManager(
    private val mNetworkManager: NetworkManagerHelper,
    private val mWebSocketConnector: WebSocketConnectorHelper
) : RemoteManagerHelper {

    override fun openConnection(token: String): Flow<BaseResponse> {
        return mWebSocketConnector.openConnection(token)
    }

    override fun closeConnection() {
        mWebSocketConnector.closeConnection()
    }

    override fun subscribeItem(symbol: String, openPrice: Double) {
        mWebSocketConnector.subscribeItem(symbol, openPrice)
    }

    override fun loadStockCandles(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): Flow<BaseResponse> {
        return mNetworkManager.loadStockCandles(symbol, from, to, resolution)
    }

    override fun loadCompanyProfile(symbol: String): Flow<BaseResponse> {
        return mNetworkManager.loadCompanyProfile(symbol)
    }

    override fun loadStockSymbols(): Flow<BaseResponse> {
        return mNetworkManager.loadStockSymbols()
    }

    override fun loadCompanyNews(symbol: String, from: String, to: String): Flow<BaseResponse> {
        return mNetworkManager.loadCompanyNews(symbol, from, to)
    }

    override fun loadCompanyQuote(symbol: String, position: Int): Flow<BaseResponse> {
        return mNetworkManager.loadCompanyQuote(symbol, position)
    }
}