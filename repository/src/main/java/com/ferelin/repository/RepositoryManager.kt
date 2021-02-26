package com.ferelin.repository

import android.content.Context
import com.ferelin.local.LocalManager
import com.ferelin.local.database.CompaniesDatabase
import com.ferelin.local.database.CompaniesManager
import com.ferelin.local.json.JsonManager
import com.ferelin.remote.RemoteManager
import com.ferelin.remote.network.NetworkManager
import com.ferelin.remote.webSocket.WebSocketConnector
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.tools.local.LocalManagerTools
import com.ferelin.repository.tools.local.LocalManagerToolsHelper
import com.ferelin.repository.tools.remote.RemoteManagerTools
import com.ferelin.repository.tools.remote.RemoteManagerToolsHelper
import com.ferelin.repository.tools.remote.ResponsesConfigurator
import com.ferelin.repository.utilits.Response
import com.ferelin.shared.SingletonHolder
import kotlinx.coroutines.flow.Flow

class RepositoryManager private constructor(
    private val mRemoteManagerToolsHelper: RemoteManagerToolsHelper,
    private val mLocalManagerToolsHelper: LocalManagerToolsHelper
) : RepositoryManagerHelper {

    override fun openConnection(dataToSubscribe: Collection<String>): Flow<Response<HashMap<String, Any>>> {
        return mRemoteManagerToolsHelper.openConnection(dataToSubscribe)
    }

    override fun loadStockCandles(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): Flow<Response<HashMap<String, Any>>> {
        return mRemoteManagerToolsHelper.loadStockCandles(symbol, from, to, resolution)
    }

    override fun loadCompanyProfile(symbol: String): Flow<Response<AdaptiveCompany>> {
        return mRemoteManagerToolsHelper.loadCompanyProfile(symbol)
    }

    override fun loadStockSymbols(): Flow<Response<List<String>>> {
        return mRemoteManagerToolsHelper.loadStockSymbols()
    }

    override fun getAllCompanies(): Flow<List<AdaptiveCompany>> {
        return mLocalManagerToolsHelper.getAllCompanies()
    }

    override fun insertCompany(company: AdaptiveCompany) {
        mLocalManagerToolsHelper.insertCompany(company)
    }

    companion object : SingletonHolder<RepositoryManager, Context>({
        val remoteHelper = RemoteManager(NetworkManager(), WebSocketConnector())
        val remoteTools = RemoteManagerTools(remoteHelper, ResponsesConfigurator())

        val dataBase = CompaniesDatabase.getInstance(it)
        val localHelper = LocalManager(JsonManager(it), CompaniesManager(dataBase))
        val localTools = LocalManagerTools(localHelper)

        RepositoryManager(remoteTools, localTools)
    })
}