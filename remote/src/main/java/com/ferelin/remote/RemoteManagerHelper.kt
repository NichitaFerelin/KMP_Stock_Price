package com.ferelin.remote

import com.ferelin.remote.network.companyProfile.CompanyProfileResponse
import com.ferelin.remote.network.stockCandles.StockCandlesResponse
import com.ferelin.remote.network.stockSymbol.StockSymbolResponse
import com.ferelin.remote.webSocket.WebSocketResponse
import kotlinx.coroutines.flow.Flow

interface RemoteManagerHelper {

    fun openConnection(): Flow<WebSocketResponse>
    fun loadStockCandles(symbol: String, from: Double, to: Double): Flow<StockCandlesResponse>
    fun loadCompanyProfile(symbol: String): Flow<CompanyProfileResponse>
    fun checkUpdates(previousLoadedSymbols: Collection<String>): Flow<List<StockSymbolResponse>>
}