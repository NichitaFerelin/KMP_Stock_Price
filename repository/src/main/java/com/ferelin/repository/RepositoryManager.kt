package com.ferelin.repository

import android.content.Context
import com.ferelin.local.LocalManagerHelper
import com.ferelin.local.model.Company
import com.ferelin.remote.RemoteManager
import com.ferelin.remote.network.stockCandles.StockCandlesResponse
import com.ferelin.remote.network.stockSymbol.StockSymbolResponse
import com.ferelin.remote.webSocket.WebSocketResponse
import kotlinx.coroutines.flow.Flow

class RepositoryManager(
    private val mLocalManagerHelper: LocalManagerHelper,
    private val mRemoteManagerHelper: RemoteManager
) : RepositoryManagerHelper {

    override fun openConnection(): Flow<WebSocketResponse> {
        return mRemoteManagerHelper.openConnection()
    }

    override fun loadStockCandles(
        symbol: String,
        from: Double,
        to: Double
    ): Flow<StockCandlesResponse> {
        TODO()
    }

    override fun getData(context: Context): Flow<List<Company>> {
        return mLocalManagerHelper.getData(context)
    }

    override fun insertData(data: List<Company>) {
        mLocalManagerHelper.insertData(data)
    }

    override fun insert(company: Company) {
        mLocalManagerHelper.insert(company)
    }

    override fun checkUpdates(previousLoadedSymbols: Collection<String>): Flow<List<StockSymbolResponse>> {
        return mRemoteManagerHelper.checkUpdates(previousLoadedSymbols) // TODO Save updates
    }
}