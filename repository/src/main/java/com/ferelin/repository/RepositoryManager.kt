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

import android.app.Activity
import com.ferelin.local.LocalManagerHelper
import com.ferelin.remote.RemoteMediatorHelper
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.responseConverter.ResponseConverterHelper
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.repository.utils.RepositoryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class RepositoryManager @Inject constructor(
    private val mRemoteMediatorHelper: RemoteMediatorHelper,
    private val mLocalManagerHelper: LocalManagerHelper,
    private val mResponseConverterHelper: ResponseConverterHelper
) : RepositoryManagerHelper {

    override fun getAllCompanies(): Flow<RepositoryResponse<List<AdaptiveCompany>>> {
        return mLocalManagerHelper.getAllCompaniesAsResponse().map {
            mResponseConverterHelper.convertCompaniesResponse(it)
        }
    }

    override fun openWebSocketConnection(): Flow<RepositoryResponse<AdaptiveWebSocketPrice>> {
        return mRemoteMediatorHelper.openWebSocketConnection().map {
            mResponseConverterHelper.convertWebSocketResponse(it)
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
            mResponseConverterHelper.convertStockCandlesResponse(it, symbol)
        }
    }

    override fun loadCompanyProfile(symbol: String): Flow<RepositoryResponse<AdaptiveCompanyProfile>> {
        return mRemoteMediatorHelper.loadCompanyProfile(symbol).map {
            mResponseConverterHelper.convertCompanyProfileResponse(it, symbol)
        }
    }

    override fun loadStockSymbols(): Flow<RepositoryResponse<AdaptiveStocksSymbols>> {
        return mRemoteMediatorHelper.loadStockSymbols().map {
            mResponseConverterHelper.convertStockSymbolsResponse(it)
        }
    }

    override fun loadCompanyNews(
        symbol: String,
        from: String,
        to: String
    ): Flow<RepositoryResponse<AdaptiveCompanyNews>> {
        return mRemoteMediatorHelper.loadCompanyNews(symbol, from, to).map {
            mResponseConverterHelper.convertCompanyNewsResponse(it, symbol)
        }
    }

    override fun loadCompanyQuote(
        symbol: String,
        position: Int,
        isImportant: Boolean
    ): Flow<RepositoryResponse<AdaptiveCompanyDayData>> {
        return mRemoteMediatorHelper.loadCompanyQuote(symbol, position, isImportant).map {
            mResponseConverterHelper.convertCompanyQuoteResponse(it)
        }
    }

    override fun saveCompanyData(adaptiveCompany: AdaptiveCompany) {
        val preparedForInsert = mResponseConverterHelper.convertCompanyForInsert(adaptiveCompany)
        mLocalManagerHelper.updateCompany(preparedForInsert)
    }

    override fun getSearchesHistory(): Flow<RepositoryResponse<List<AdaptiveSearchRequest>>> {
        return mLocalManagerHelper.getSearchesHistoryAsResponse().map {
            mResponseConverterHelper.convertSearchesForResponse(it)
        }
    }

    override suspend fun setSearchesHistory(requests: List<AdaptiveSearchRequest>) {
        val preparedForInsert = mResponseConverterHelper.convertSearchesForInsert(requests)
        mLocalManagerHelper.setSearchesHistory(preparedForInsert)
    }

    override fun getFirstTimeLaunchState(): Flow<RepositoryResponse<Boolean>> {
        return mLocalManagerHelper.getFirstTimeLaunchState().map {
            mResponseConverterHelper.convertFirstTimeLaunchStateToResponse(it)
        }
    }

    override suspend fun setFirstTimeLaunchState(state: Boolean) {
        mLocalManagerHelper.setFirstTimeLaunchState(state)
    }

    override suspend fun clearSearchesHistory() {
        mLocalManagerHelper.clearSearchesHistory()
    }

    override fun tryToSignIn(
        holderActivity: Activity,
        phone: String
    ): Flow<RepositoryResponse<RepositoryMessages>> {
        return mRemoteMediatorHelper.tryToLogIn(holderActivity, phone).map {
            mResponseConverterHelper.convertAuthenticationResponse(it)
        }
    }

    override fun logInWithCode(code: String) {
        mRemoteMediatorHelper.logInWithCode(code)
    }

    override fun provideUserId(): String? {
        return mRemoteMediatorHelper.provideUserId()
    }

    override fun provideIsUserLogged(): Boolean {
        return mRemoteMediatorHelper.provideIsUserLogged()
    }

    override fun logOut() {
        mRemoteMediatorHelper.logOut()
    }

    override fun eraseCompanyFromRealtimeDb(userId: String, companyId: String) {
        mRemoteMediatorHelper.eraseCompanyIdFromRealtimeDb(userId, companyId)
    }

    override fun writeCompanyIdToRealtimeDb(userId: String, companyId: String) {
        mRemoteMediatorHelper.writeCompanyIdToRealtimeDb(userId, companyId)
    }

    override fun writeCompaniesIdsToDb(userId: String, companiesId: List<String>) {
        mRemoteMediatorHelper.writeCompaniesIdsToDb(userId, companiesId)
    }

    override fun writeSearchRequestToDb(userId: String, searchRequest: String) {
        mRemoteMediatorHelper.writeSearchRequestToDb(userId, searchRequest)
    }

    override fun writeSearchRequestsToDb(userId: String, searchRequests: List<String>) {
        mRemoteMediatorHelper.writeSearchRequestsToDb(userId, searchRequests)
    }

    override fun readSearchRequestsFromDb(userId: String): Flow<RepositoryResponse<String?>> {
        return mRemoteMediatorHelper.readSearchRequestsFromDb(userId).map { response ->
            mResponseConverterHelper.convertRealtimeDatabaseResponse(response)
        }
    }

    override fun readCompaniesIdsFromDb(userId: String): Flow<RepositoryResponse<String?>> {
        return mRemoteMediatorHelper.readCompaniesIdsFromDb(userId).map { response ->
            mResponseConverterHelper.convertRealtimeDatabaseResponse(response)
        }
    }

    override fun eraseSearchRequestFromDb(userId: String, searchRequest: String) {
        mRemoteMediatorHelper.eraseSearchRequestFromDb(userId, searchRequest)
    }
}