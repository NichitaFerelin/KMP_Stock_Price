package com.ferelin.repository

import android.content.Context
import com.ferelin.local.LocalManager
import com.ferelin.local.LocalManagerHelper
import com.ferelin.local.databases.companies.CompaniesDatabase
import com.ferelin.local.databases.companies.CompaniesManager
import com.ferelin.local.databases.searchesHistory.SearchesHistoryDatabase
import com.ferelin.local.databases.searchesHistory.SearchesHistoryManager
import com.ferelin.local.json.JsonManager
import com.ferelin.remote.RemoteManager
import com.ferelin.remote.RemoteManagerHelper
import com.ferelin.remote.network.NetworkManager
import com.ferelin.remote.webSocket.WebSocketConnector
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.dataConverter.DataConverter
import com.ferelin.repository.dataConverter.DataConverterHelper
import com.ferelin.repository.utilits.RepositoryResponse
import com.ferelin.shared.SingletonHolder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RepositoryManager private constructor(
    private val mRemoteManagerHelper: RemoteManagerHelper,
    private val mLocalManagerHelper: LocalManagerHelper,
    private val mDataConverterHelper: DataConverterHelper
) : RepositoryManagerHelper {

    override fun getAllCompanies(): Flow<RepositoryResponse<List<AdaptiveCompany>>> {
        return mLocalManagerHelper.getAllCompaniesAsResponse().map {
            mDataConverterHelper.convertDatabaseCompanies(it) {
                val preparedForInsert = mDataConverterHelper.convertCompaniesForInsert(it)
                mLocalManagerHelper.insertAllCompanies(preparedForInsert)
            }
        }
    }

    override fun openConnection(): Flow<RepositoryResponse<AdaptiveLastPrice>> {
        return mRemoteManagerHelper.openConnection().map {
            mDataConverterHelper.convertWebSocketResponse(it)
        }
    }

    override fun subscribeItem(symbol: String) {
        mRemoteManagerHelper.subscribeItem(symbol)
    }

    override fun loadStockCandles(
        company: AdaptiveCompany,
        position: Int,
        from: Long,
        to: Long,
        resolution: String
    ): Flow<RepositoryResponse<AdaptiveStockCandle>> {
        return mRemoteManagerHelper.loadStockCandle(
            company.symbol,
            position,
            from,
            to,
            resolution
        ).map {
            mDataConverterHelper.convertStockCandleResponse(it)
        }
    }

    override fun loadCompanyProfile(symbol: String): Flow<RepositoryResponse<AdaptiveCompanyProfile>> {
        return mRemoteManagerHelper.loadCompanyProfile(symbol).map {
            mDataConverterHelper.convertCompanyProfileResponse(it, symbol) {
                mLocalManagerHelper.insertCompany(it)
            }
        }
    }

    override fun loadStockSymbols(): Flow<RepositoryResponse<AdaptiveStockSymbols>> {
        return mRemoteManagerHelper.loadStockSymbols().map {
            mDataConverterHelper.convertStockSymbolsResponse(it)
        }
    }

    override fun updateCompany(adaptiveCompany: AdaptiveCompany) {
        val preparedForInsert = mDataConverterHelper.convertCompanyForInsert(adaptiveCompany)
        mLocalManagerHelper.updateCompany(preparedForInsert)
    }

    override fun getSearchesHistory(): Flow<RepositoryResponse<List<AdaptiveSearch>>> {
        return mLocalManagerHelper.getSearchesHistory().map {
            mDataConverterHelper.convertSearchesForResponse(it)
        }
    }

    override fun getPopularSearches(): List<AdaptiveSearch> {
        return mLocalManagerHelper.getPopularSearches().map {
            mDataConverterHelper.convertSearchForResponse(it)
        }
    }

    override fun insertSearch(search: AdaptiveSearch) {
        val preparedForInsert = mDataConverterHelper.convertSearchForInsert(search)
        mLocalManagerHelper.insertSearch(preparedForInsert)
    }

    companion object : SingletonHolder<RepositoryManager, Context>({
        val remoteHelper = RemoteManager(NetworkManager(), WebSocketConnector())
        val dataBase = CompaniesDatabase.getInstance(it)

        val searchesDatabase = SearchesHistoryDatabase.getInstance(it)
        val searchesHelper = SearchesHistoryManager(searchesDatabase)

        val localHelper = LocalManager(JsonManager(it), CompaniesManager(dataBase), searchesHelper)
        val dataConverter = DataConverter()
        RepositoryManager(remoteHelper, localHelper, dataConverter)
    })
}