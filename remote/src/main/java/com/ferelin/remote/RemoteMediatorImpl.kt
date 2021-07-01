package com.ferelin.remote

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
import com.ferelin.remote.api.ApiManager
import com.ferelin.remote.api.companyNews.CompanyNewsResponse
import com.ferelin.remote.api.companyProfile.CompanyProfileResponse
import com.ferelin.remote.api.companyQuote.CompanyQuoteResponse
import com.ferelin.remote.api.stockCandles.StockCandlesResponse
import com.ferelin.remote.api.stockSymbols.StockSymbolResponse
import com.ferelin.remote.auth.AuthenticationManager
import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.database.RealtimeDatabase
import com.ferelin.remote.webSocket.connector.WebSocketConnector
import com.ferelin.remote.webSocket.response.WebSocketResponse
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

/*
* Providing requests to right entity
* */
@Singleton
class RemoteMediatorImpl @Inject constructor(
    private val mApiManager: ApiManager,
    private val mWebSocketConnector: WebSocketConnector,
    private val mAuthenticationManager: AuthenticationManager,
    private val mRealtimeDatabaseManager: RealtimeDatabase,
) : RemoteMediator {

    override fun openWebSocketConnection(token: String): Flow<BaseResponse<WebSocketResponse>> {
        return mWebSocketConnector.openWebSocketConnection(token)
    }

    override fun closeWebSocketConnection() {
        mWebSocketConnector.closeWebSocketConnection()
    }

    override fun subscribeItemOnLiveTimeUpdates(symbol: String, previousPrice: Double) {
        mWebSocketConnector.subscribeItemOnLiveTimeUpdates(symbol, previousPrice)
    }

    override fun unsubscribeItemFromLiveTimeUpdates(symbol: String) {
        mWebSocketConnector.unsubscribeItemFromLiveTimeUpdates(symbol)
    }

    override fun loadStockCandles(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): Flow<BaseResponse<StockCandlesResponse>> {
        return mApiManager.loadStockCandles(symbol, from, to, resolution)
    }

    override fun loadCompanyProfile(symbol: String): Flow<BaseResponse<CompanyProfileResponse>> {
        return mApiManager.loadCompanyProfile(symbol)
    }

    override fun loadStockSymbols(): Flow<BaseResponse<StockSymbolResponse>> {
        return mApiManager.loadStockSymbols()
    }

    override fun loadCompanyNews(
        symbol: String,
        from: String,
        to: String
    ): Flow<BaseResponse<List<CompanyNewsResponse>>> {
        return mApiManager.loadCompanyNews(symbol, from, to)
    }

    override fun loadCompanyQuote(
        symbol: String,
        position: Int,
        isImportant: Boolean
    ): Flow<BaseResponse<CompanyQuoteResponse>> {
        return mApiManager.loadCompanyQuote(symbol, position, isImportant)
    }

    override fun tryToLogIn(holderActivity: Activity, phone: String): Flow<BaseResponse<Boolean>> {
        return mAuthenticationManager.tryToLogIn(holderActivity, phone)
    }

    override fun logInWithCode(code: String) {
        mAuthenticationManager.logInWithCode(code)
    }

    override fun logOut() {
        mAuthenticationManager.logOut()
    }

    override fun provideUserId(): String? {
        return mAuthenticationManager.provideUserId()
    }

    override fun provideIsUserLogged(): Boolean {
        return mAuthenticationManager.provideIsUserLogged()
    }

    override fun eraseCompanyIdFromRealtimeDb(userId: String, companyId: String) {
        mRealtimeDatabaseManager.eraseCompanyIdFromRealtimeDb(userId, companyId)
    }

    override fun writeCompanyIdToRealtimeDb(userId: String, companyId: String) {
        mRealtimeDatabaseManager.writeCompanyIdToRealtimeDb(userId, companyId)
    }

    override fun writeCompaniesIdsToDb(userId: String, companiesId: List<String>) {
        mRealtimeDatabaseManager.writeCompaniesIdsToDb(userId, companiesId)
    }

    override fun readCompaniesIdsFromDb(userId: String): Flow<BaseResponse<String?>> {
        return mRealtimeDatabaseManager.readCompaniesIdsFromDb(userId)
    }

    override fun writeSearchRequestToDb(userId: String, searchRequest: String) {
        mRealtimeDatabaseManager.writeSearchRequestToDb(userId, searchRequest)
    }

    override fun writeSearchRequestsToDb(userId: String, searchRequests: List<String>) {
        mRealtimeDatabaseManager.writeSearchRequestsToDb(userId, searchRequests)
    }

    override fun readSearchRequestsFromDb(userId: String): Flow<BaseResponse<String?>> {
        return mRealtimeDatabaseManager.readSearchRequestsFromDb(userId)
    }

    override fun eraseSearchRequestFromDb(userId: String, searchRequest: String) {
        mRealtimeDatabaseManager.eraseSearchRequestFromDb(userId, searchRequest)
    }

    override fun cacheChat(currentUserNumber: String, associatedUserNumber: String) {
        mRealtimeDatabaseManager.cacheChat(currentUserNumber, associatedUserNumber)
    }

    override fun getUserChats(userNumber: String): Flow<BaseResponse<String>> {
        return mRealtimeDatabaseManager.getUserChats(userNumber)
    }

    override fun getMessagesForChat(
        currentUserNumber: String,
        associatedUserNumber: String
    ): Flow<BaseResponse<HashMap<String, Any>>> {
        return mRealtimeDatabaseManager.getMessagesForChat(currentUserNumber, associatedUserNumber)
    }

    override fun cacheMessage(
        currentUserNumber: String,
        associatedUserNumber: String,
        messageText: String,
        messageSideKey: Char
    ) {
        mRealtimeDatabaseManager.cacheMessage(
            currentUserNumber,
            associatedUserNumber,
            messageText,
            messageSideKey
        )
    }
}