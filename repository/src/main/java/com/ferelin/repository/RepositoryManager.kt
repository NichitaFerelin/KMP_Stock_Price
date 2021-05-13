package com.ferelin.repository

/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.ferelin.local.LocalManagerHelper
import com.ferelin.remote.RemoteMediatorHelper
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.dataConverter.DataConverterHelper
import com.ferelin.repository.utils.RepositoryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class RepositoryManager @Inject constructor(
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
        position: Int,
        isImportant: Boolean
    ): Flow<RepositoryResponse<AdaptiveCompanyDayData>> {
        return mRemoteMediatorHelper.loadCompanyQuote(symbol, position, isImportant).map {
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
}