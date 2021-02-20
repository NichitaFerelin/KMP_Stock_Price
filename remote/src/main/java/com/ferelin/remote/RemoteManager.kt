package com.ferelin.remote

import com.ferelin.remote.network.NetworkManagerHelper
import com.ferelin.remote.network.companyProfile.CompanyProfileResponse
import com.ferelin.remote.network.stockCandles.StockCandlesResponse
import com.ferelin.remote.network.stockSymbol.StockSymbolResponse
import com.ferelin.remote.utilits.Api
import com.ferelin.remote.webSocket.WebSocketConnectorHelper
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
    private val mNetworkManager: NetworkManagerHelper,
    private val mWebSocketConnector: WebSocketConnectorHelper
) : RemoteManagerHelper {

    override fun openConnection(dataToSubscribe: Collection<String>): Flow<WebSocketResponse> =
        flow {
            mWebSocketConnector.openConnection(dataToSubscribe, Api.FINNHUB_TOKEN).collect {
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