package com.ferelin.remote

import com.ferelin.remote.network.NetworkManager
import com.ferelin.remote.network.companyProfile.CompanyProfileResponse
import com.ferelin.remote.network.stockCandles.StockCandlesResponse
import com.ferelin.remote.network.stockSymbol.StockSymbolResponse
import com.ferelin.remote.webSocket.WebSocketConnector
import com.ferelin.remote.webSocket.WebSocketResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn

/*
*  TODO Configure response codes
* */

class RemoteManager(
    private val mNetworkManager: NetworkManager,
    private val mWebSocketConnector: WebSocketConnector
) : RemoteManagerHelper {

    override fun openConnection(): Flow<WebSocketResponse> = flow {
        mWebSocketConnector.openConnection().collect {
            emit(it)
        }
    }.flowOn(Dispatchers.IO)

    override fun loadStockCandles(
        symbol: String,
        from: Double,
        to: Double
    ): Flow<StockCandlesResponse> = flow {
        mNetworkManager.loadStockCandle(symbol, from, to).collect {
            emit(it)
        }
    }.flowOn(Dispatchers.IO)

    override fun loadCompanyProfile(symbol: String): Flow<CompanyProfileResponse> = flow {
        mNetworkManager.loadCompanyProfile(symbol).collect {
            emit(it)
        }
    }.flowOn(Dispatchers.IO)

    override fun checkUpdates(
        previousLoadedSymbols: Collection<String>
    ): Flow<List<StockSymbolResponse>> = flow {
        mNetworkManager.checkUpdates(previousLoadedSymbols).collect {
            emit(it)
        }
    }.flowOn(Dispatchers.IO)
}