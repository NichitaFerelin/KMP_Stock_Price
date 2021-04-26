package com.ferelin.repository

import android.content.Context
import com.ferelin.local.LocalManager
import com.ferelin.local.LocalManagerHelper
import com.ferelin.local.database.CompaniesDatabase
import com.ferelin.local.database.CompaniesManager
import com.ferelin.local.json.JsonManager
import com.ferelin.local.preferences.StorePreferences
import com.ferelin.remote.RemoteMediator
import com.ferelin.remote.RemoteMediatorHelper
import com.ferelin.remote.network.NetworkManager
import com.ferelin.remote.webSocket.WebSocketConnector
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.dataConverter.DataAdapter
import com.ferelin.repository.dataConverter.DataConverter
import com.ferelin.repository.dataConverter.DataConverterHelper
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.shared.SingletonHolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RepositoryManager(
    private val mRemoteMediatorHelper: RemoteMediatorHelper,
    private val mLocalManagerHelper: LocalManagerHelper,
    private val mDataConverterHelper: DataConverterHelper
) : RepositoryManagerHelper {

    override fun getAllCompanies(): Flow<RepositoryResponse<List<AdaptiveCompany>>> {
        return mLocalManagerHelper.getAllCompaniesAsResponse().map {
            mDataConverterHelper.convertCompaniesResponse(it)
        }
    }

    override fun openWebSocketConnection(): Flow<RepositoryResponse<AdaptiveWebSocketPrice>> {
        return mRemoteMediatorHelper.openWebSocketConnection().map {
            mDataConverterHelper.convertWebSocketResponse(it)
        }
    }

    override fun invalidateWebSocketConnection() {
        mRemoteMediatorHelper.closeWebSocketConnection()
    }

    override fun subscribeItemToLiveTimeUpdates(symbol: String, openPrice: Double) {
        mRemoteMediatorHelper.subscribeItemOnLiveTimeUpdates(symbol, openPrice)
    }

    override fun unsubscribeItemFromLiveTimeUpdates(symbol: String) {
        mRemoteMediatorHelper.unsubscribeItemFromLiveTimeUpdates(symbol)
    }

    override fun loadStockCandles(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): Flow<RepositoryResponse<AdaptiveCompanyHistory>> {
        return mRemoteMediatorHelper.loadStockCandles(symbol, from, to, resolution).map {
            mDataConverterHelper.convertStockCandlesResponse(it, symbol)
        }
    }

    override fun loadCompanyProfile(symbol: String): Flow<RepositoryResponse<AdaptiveCompanyProfile>> {
        return mRemoteMediatorHelper.loadCompanyProfile(symbol).map {
            mDataConverterHelper.convertCompanyProfileResponse(it, symbol)
        }
    }

    override fun loadStockSymbols(): Flow<RepositoryResponse<AdaptiveStocksSymbols>> {
        return mRemoteMediatorHelper.loadStockSymbols().map {
            mDataConverterHelper.convertStockSymbolsResponse(it)
        }
    }

    override fun loadCompanyNews(
        symbol: String,
        from: String,
        to: String
    ): Flow<RepositoryResponse<AdaptiveCompanyNews>> {
        return mRemoteMediatorHelper.loadCompanyNews(symbol, from, to).map {
            mDataConverterHelper.convertCompanyNewsResponse(it, symbol)
        }
    }

    override fun loadCompanyQuote(
        symbol: String,
        position: Int
    ): Flow<RepositoryResponse<AdaptiveCompanyDayData>> {
        return mRemoteMediatorHelper.loadCompanyQuote(symbol, position).map {
            mDataConverterHelper.convertCompanyQuoteResponse(it)
        }
    }

    override fun saveCompanyData(adaptiveCompany: AdaptiveCompany) {
        val preparedForInsert = mDataConverterHelper.convertCompanyForInsert(adaptiveCompany)
        mLocalManagerHelper.updateCompany(preparedForInsert)
    }

    override fun getSearchesHistory(): Flow<RepositoryResponse<List<AdaptiveSearchRequest>>> {
        return mLocalManagerHelper.getSearchesHistoryAsResponse().map {
            mDataConverterHelper.convertSearchesForResponse(it)
        }
    }

    override suspend fun setSearchesHistory(requests: List<AdaptiveSearchRequest>) {
        val preparedForInsert = mDataConverterHelper.convertSearchesForInsert(requests)
        mLocalManagerHelper.setSearchesHistory(preparedForInsert)
    }

    override fun getFirstTimeLaunchState(): Flow<RepositoryResponse<Boolean>> {
        return mLocalManagerHelper.getFirstTimeLaunchState().map {
            mDataConverterHelper.convertFirstTimeLaunchStateToResponse(it)
        }
    }

    override suspend fun setFirstTimeLaunchState(state: Boolean) {
        mLocalManagerHelper.setFirstTimeLaunchState(state)
    }

    companion object : SingletonHolder<RepositoryManager, Context>({
        val remoteHelper = RemoteMediator(NetworkManager(), WebSocketConnector())
        val dataBase = CompaniesDatabase.getInstance(it)
        val preferences = StorePreferences(it)
        val localHelper = LocalManager(JsonManager(it), CompaniesManager(dataBase), preferences)
        val dataConverter = DataConverter(DataAdapter())
        RepositoryManager(remoteHelper, localHelper, dataConverter)
    })
}