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
import com.ferelin.local.LocalManager
import com.ferelin.remote.RemoteMediator
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.converter.ResponseConverter
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.repository.utils.RepositoryResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class RepositoryImpl @Inject constructor(
    private val mRemoteMediator: RemoteMediator,
    private val mLocalManager: LocalManager,
    private val mResponseConverter: ResponseConverter
) : Repository {

    override fun getAllCompanies(): RepositoryResponse<List<AdaptiveCompany>> {
        val localCompanies = mLocalManager.getAllCompanies()
        return mResponseConverter.convertCompaniesResponse(localCompanies)
    }

    override fun openWebSocketConnection(): Flow<RepositoryResponse<AdaptiveWebSocketPrice>> {
        return mRemoteMediator.openWebSocketConnection().map {
            mResponseConverter.convertWebSocketResponse(it)
        }
    }

    override fun invalidateWebSocketConnection() {
        mRemoteMediator.closeWebSocketConnection()
    }

    override fun subscribeItemToLiveTimeUpdates(symbol: String, openPrice: Double) {
        mRemoteMediator.subscribeItemOnLiveTimeUpdates(symbol, openPrice)
    }

    override fun unsubscribeItemFromLiveTimeUpdates(symbol: String) {
        mRemoteMediator.unsubscribeItemFromLiveTimeUpdates(symbol)
    }

    override fun loadStockCandles(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): Flow<RepositoryResponse<AdaptiveCompanyHistory>> {
        return mRemoteMediator.loadStockCandles(symbol, from, to, resolution).map {
            mResponseConverter.convertStockCandlesResponse(it, symbol)
        }
    }

    override fun loadCompanyProfile(symbol: String): Flow<RepositoryResponse<AdaptiveCompanyProfile>> {
        return mRemoteMediator.loadCompanyProfile(symbol).map {
            mResponseConverter.convertCompanyProfileResponse(it, symbol)
        }
    }

    override fun loadStockSymbols(): Flow<RepositoryResponse<AdaptiveStocksSymbols>> {
        return mRemoteMediator.loadStockSymbols().map {
            mResponseConverter.convertStockSymbolsResponse(it)
        }
    }

    override fun loadCompanyNews(
        symbol: String,
        from: String,
        to: String
    ): Flow<RepositoryResponse<AdaptiveCompanyNews>> {
        return mRemoteMediator.loadCompanyNews(symbol, from, to).map {
            mResponseConverter.convertCompanyNewsResponse(it, symbol)
        }
    }

    override fun loadCompanyQuote(
        symbol: String,
        position: Int,
        isImportant: Boolean
    ): Flow<RepositoryResponse<AdaptiveCompanyDayData>> {
        return mRemoteMediator.loadCompanyQuote(symbol, position, isImportant).map {
            mResponseConverter.convertCompanyQuoteResponse(it)
        }
    }

    override fun saveCompanyData(adaptiveCompany: AdaptiveCompany) {
        val preparedForInsert = mResponseConverter.convertCompanyForInsert(adaptiveCompany)
        mLocalManager.updateCompany(preparedForInsert)
    }

    override suspend fun getSearchesHistory(): RepositoryResponse<List<AdaptiveSearchRequest>> {
        val searchesHistory = mLocalManager.getSearchesHistoryAsResponse()
        return mResponseConverter.convertSearchesForResponse(searchesHistory)
    }

    override suspend fun setSearchesHistory(requests: List<AdaptiveSearchRequest>) {
        val preparedForInsert = mResponseConverter.convertSearchesForInsert(requests)
        mLocalManager.setSearchRequestsHistory(preparedForInsert)
    }

    override suspend fun getFirstTimeLaunchState(): RepositoryResponse<Boolean> {
        val firstTimeLaunch = mLocalManager.getFirstTimeLaunchState()
        return mResponseConverter.convertFirstTimeLaunchStateToResponse(firstTimeLaunch)
    }

    override suspend fun setFirstTimeLaunchState(state: Boolean) {
        mLocalManager.setFirstTimeLaunchState(state)
    }

    override suspend fun clearSearchesHistory() {
        mLocalManager.clearSearchRequestsHistory()
    }

    override fun tryToSignIn(
        holderActivity: Activity,
        phone: String
    ): Flow<RepositoryResponse<RepositoryMessages>> {
        return mRemoteMediator.tryToLogIn(holderActivity, phone).map {
            mResponseConverter.convertAuthenticationResponse(it)
        }
    }

    override fun logInWithCode(code: String) {
        mRemoteMediator.logInWithCode(code)
    }

    override fun provideUserId(): String? {
        return mRemoteMediator.provideUserId()
    }

    override fun provideIsUserLogged(): Boolean {
        return mRemoteMediator.provideIsUserLogged()
    }

    override fun logOut() {
        mRemoteMediator.logOut()
    }

    override fun eraseCompanyFromRealtimeDb(userId: String, companyId: String) {
        mRemoteMediator.eraseCompanyIdFromRealtimeDb(userId, companyId)
    }

    override fun writeCompanyIdToRealtimeDb(userId: String, companyId: String) {
        mRemoteMediator.writeCompanyIdToRealtimeDb(userId, companyId)
    }

    override fun writeCompaniesIdsToDb(userId: String, companiesId: List<String>) {
        mRemoteMediator.writeCompaniesIdsToDb(userId, companiesId)
    }

    override fun writeSearchRequestToDb(userId: String, searchRequest: String) {
        mRemoteMediator.writeSearchRequestToDb(userId, searchRequest)
    }

    override fun writeSearchRequestsToDb(userId: String, searchRequests: List<String>) {
        mRemoteMediator.writeSearchRequestsToDb(userId, searchRequests)
    }

    override fun readSearchRequestsFromDb(userId: String): Flow<RepositoryResponse<String?>> {
        return mRemoteMediator.readSearchRequestsFromDb(userId).map { response ->
            mResponseConverter.convertRealtimeDatabaseResponse(response)
        }
    }

    override fun readCompaniesIdsFromDb(userId: String): Flow<RepositoryResponse<String?>> {
        return mRemoteMediator.readCompaniesIdsFromDb(userId).map { response ->
            mResponseConverter.convertRealtimeDatabaseResponse(response)
        }
    }

    override fun eraseSearchRequestFromDb(userId: String, searchRequest: String) {
        mRemoteMediator.eraseSearchRequestFromDb(userId, searchRequest)
    }
}