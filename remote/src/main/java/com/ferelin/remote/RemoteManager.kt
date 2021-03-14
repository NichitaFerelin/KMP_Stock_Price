package com.ferelin.remote

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.network.NetworkManagerHelper
import com.ferelin.remote.network.companyNews.CompanyNewsResponse
import com.ferelin.remote.network.companyProfile.CompanyProfileResponse
import com.ferelin.remote.network.companyQuote.CompanyQuoteResponse
import com.ferelin.remote.network.stockCandles.StockCandlesResponse
import com.ferelin.remote.network.stockSymbols.StockSymbolResponse
import com.ferelin.remote.webSocket.WebSocketConnectorHelper
import com.ferelin.remote.webSocket.WebSocketResponse
import kotlinx.coroutines.flow.Flow

class RemoteManager(
    private val mNetworkManager: NetworkManagerHelper,
    private val mWebSocketConnector: WebSocketConnectorHelper
) : RemoteManagerHelper {

    override fun openConnection(token: String): Flow<BaseResponse<WebSocketResponse>> {
        return mWebSocketConnector.openConnection(token)
    }

    override fun closeConnection() {
        mWebSocketConnector.closeConnection()
    }

    override fun subscribeItem(symbol: String, openPrice: Double) {
        mWebSocketConnector.subscribeItem(symbol, openPrice)
    }

    override fun unsubscribeItem(symbol: String) {
        mWebSocketConnector.unsubscribeItem(symbol)
    }

    override fun loadStockCandles(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): Flow<BaseResponse<StockCandlesResponse>> {
        return mNetworkManager.loadStockCandles(symbol, from, to, resolution)
    }

    override fun loadCompanyProfile(symbol: String): Flow<BaseResponse<CompanyProfileResponse>> {
        return mNetworkManager.loadCompanyProfile(symbol)
    }

    override fun loadStockSymbols(): Flow<BaseResponse<StockSymbolResponse>> {
        return mNetworkManager.loadStockSymbols()
    }

    override fun loadCompanyNews(
        symbol: String,
        from: String,
        to: String
    ): Flow<BaseResponse<List<CompanyNewsResponse>>> {
        return mNetworkManager.loadCompanyNews(symbol, from, to)
    }

    override fun loadCompanyQuote(
        symbol: String,
        position: Int
    ): Flow<BaseResponse<CompanyQuoteResponse>> {
        return mNetworkManager.loadCompanyQuote(symbol, position)
    }
}