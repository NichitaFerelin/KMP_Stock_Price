package com.ferelin.repository

import android.content.Context
import com.ferelin.local.model.Company
import com.ferelin.remote.network.stockCandles.StockCandlesResponse
import com.ferelin.remote.network.stockSymbol.StockSymbolResponse
import com.ferelin.remote.webSocket.WebSocketResponse
import kotlinx.coroutines.flow.Flow

interface RepositoryManagerHelper {

    fun openConnection(): Flow<WebSocketResponse>

    fun loadStockCandles(symbol: String, from: Double, to: Double): Flow<StockCandlesResponse>

    fun getData(context: Context): Flow<List<Company>>

    fun insertData(data: List<Company>)

    fun insert(company: Company)

    fun checkUpdates(previousLoadedSymbols: Collection<String>): Flow<List<StockSymbolResponse>>
}