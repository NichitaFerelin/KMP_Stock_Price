package com.ferelin.repository

import android.content.Context
import com.ferelin.local.LocalManager
import com.ferelin.local.LocalManagerHelper
import com.ferelin.local.database.CompaniesDatabase
import com.ferelin.local.database.CompaniesManager
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

    override fun openConnection(
        dataToSubscribe: Collection<String>
    ): Flow<RepositoryResponse<AdaptiveLastPrice>> {
        return mRemoteManagerHelper.openConnection(dataToSubscribe).map {
            mDataConverterHelper.convertWebSocketResponse(it)
        }
    }

    override fun loadStockCandles(
        company: AdaptiveCompany,
        from: Long,
        to: Long,
        resolution: String
    ): Flow<RepositoryResponse<AdaptiveStockCandle>> {
        return mRemoteManagerHelper.loadStockCandle(company.symbol, from, to, resolution).map {
            mDataConverterHelper.convertStockCandleResponse(it, company) {
                val preparedForUpdate = mDataConverterHelper.convertCompanyForInsert(it)
                mLocalManagerHelper.updateCompany(preparedForUpdate)
            }
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

    companion object : SingletonHolder<RepositoryManager, Context>({
        val remoteHelper = RemoteManager(NetworkManager(), WebSocketConnector())
        val dataBase = CompaniesDatabase.getInstance(it)
        val localHelper = LocalManager(JsonManager(it), CompaniesManager(dataBase))
        val dataConverter = DataConverter()
        RepositoryManager(remoteHelper, localHelper, dataConverter)
    })
}