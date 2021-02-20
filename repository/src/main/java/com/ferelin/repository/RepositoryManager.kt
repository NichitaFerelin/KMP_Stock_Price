package com.ferelin.repository

import android.content.Context
import com.ferelin.local.LocalManager
import com.ferelin.local.LocalManagerHelper
import com.ferelin.local.database.CompaniesDatabase
import com.ferelin.local.database.CompaniesManager
import com.ferelin.local.json.JsonManager
import com.ferelin.local.model.Company
import com.ferelin.remote.RemoteManager
import com.ferelin.remote.RemoteManagerHelper
import com.ferelin.remote.network.NetworkManager
import com.ferelin.remote.network.stockCandles.StockCandlesResponse
import com.ferelin.remote.network.stockSymbol.StockSymbolResponse
import com.ferelin.remote.webSocket.WebSocketConnector
import com.ferelin.remote.webSocket.WebSocketResponse
import com.ferelin.shared.SingletonHolder
import kotlinx.coroutines.flow.Flow

class RepositoryManager private constructor(
    private val mRemoteManagerHelper: RemoteManagerHelper,
    private val mLocalManagerHelper: LocalManagerHelper
) : RepositoryManagerHelper {

    override fun openConnection(dataToSubscribe: Collection<String>): Flow<WebSocketResponse> {
        return mRemoteManagerHelper.openConnection(dataToSubscribe)
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

    companion object : SingletonHolder<RepositoryManager, Context>({
        val remoteHelper = RemoteManager(NetworkManager(), WebSocketConnector())
        val dataBase = CompaniesDatabase.getInstance(it)
        val localHelper = LocalManager(JsonManager(), CompaniesManager(dataBase))
        RepositoryManager(remoteHelper, localHelper)
    })
}