package com.ferelin.repository

import android.content.Context
import com.ferelin.local.LocalManager
import com.ferelin.local.LocalManagerHelper
import com.ferelin.local.database.CompaniesDatabase
import com.ferelin.local.database.CompaniesManager
import com.ferelin.local.json.JsonManager
import com.ferelin.local.prefs.StorePreferences
import com.ferelin.remote.RemoteManager
import com.ferelin.remote.RemoteManagerHelper
import com.ferelin.remote.network.NetworkManager
import com.ferelin.remote.webSocket.WebSocketConnector
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.dataConverter.DataConverter
import com.ferelin.repository.dataConverter.DataConverterHelper
import com.ferelin.repository.utilits.RepositoryResponse
import com.ferelin.repository.utilits.Time
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
            mDataConverterHelper.convertDatabaseCompanies(it) { companies ->
                val preparedForInsert = mDataConverterHelper.convertCompaniesForInsert(companies)
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
        from: Long,
        to: Long,
        resolution: String
    ): Flow<RepositoryResponse<AdaptiveStockCandles>> {
        return mRemoteManagerHelper.loadStockCandles(
            company.symbol,
            from,
            to,
            resolution
        ).map {
            mDataConverterHelper.convertStockCandleResponse(it)
        }
    }

    override fun loadCompanyProfile(symbol: String): Flow<RepositoryResponse<AdaptiveCompanyProfile>> {
        return mRemoteManagerHelper.loadCompanyProfile(symbol).map {
            mDataConverterHelper.convertCompanyProfileResponse(it, symbol) { company ->
                mLocalManagerHelper.insertCompany(company)
            }
        }
    }

    override fun loadStockSymbols(): Flow<RepositoryResponse<AdaptiveStockSymbols>> {
        return mRemoteManagerHelper.loadStockSymbols().map {
            mDataConverterHelper.convertStockSymbolsResponse(it)
        }
    }

    override fun loadCompanyNews(symbol: String): Flow<RepositoryResponse<AdaptiveCompanyNews>> {
        val dateForRequest = Time.getDataForRequest()
        return mRemoteManagerHelper.loadCompanyNews(
            symbol,
            dateForRequest.second,
            dateForRequest.first
        ).map {
            mDataConverterHelper.convertCompanyNewsResponse(it, symbol)
        }
    }

    override fun loadCompanyQuote(
        symbol: String,
        position: Int
    ): Flow<RepositoryResponse<AdaptiveCompanyQuote>> {
        return mRemoteManagerHelper.loadCompanyQuote(symbol, position).map {
            mDataConverterHelper.convertCompanyQuoteResponse(it)
        }
    }

    override fun updateCompany(adaptiveCompany: AdaptiveCompany) {
        val preparedForInsert = mDataConverterHelper.convertCompanyForInsert(adaptiveCompany)
        mLocalManagerHelper.updateCompany(preparedForInsert)
    }

    override fun getSearchesHistory(): Flow<RepositoryResponse<List<AdaptiveSearchRequest>>> {
        return mLocalManagerHelper.getSearchesHistoryAsResponse().map {
            mDataConverterHelper.convertSearchesForResponse(it)
        }
    }

    override suspend fun insertSearch(search: AdaptiveSearchRequest) {
        val preparedForInsert = search.search
        mLocalManagerHelper.addSearch(preparedForInsert)
    }

    companion object : SingletonHolder<RepositoryManager, Context>({
        val remoteHelper = RemoteManager(NetworkManager(), WebSocketConnector())
        val dataBase = CompaniesDatabase.getInstance(it)
        val preferences = StorePreferences(it)
        val localHelper = LocalManager(JsonManager(it), CompaniesManager(dataBase), preferences)
        val dataConverter = DataConverter()
        RepositoryManager(remoteHelper, localHelper, dataConverter)
    })
}