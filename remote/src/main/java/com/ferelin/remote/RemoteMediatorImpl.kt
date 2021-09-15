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

package com.ferelin.remote

import android.app.Activity
import com.ferelin.remote.api.ApiManager
import com.ferelin.remote.api.companyNews.CompanyNewsResponse
import com.ferelin.remote.api.companyProfile.CompanyProfileResponse
import com.ferelin.remote.api.stockHistory.StockHistoryResponse
import com.ferelin.remote.api.stockPrice.StockPriceResponse
import com.ferelin.remote.api.stockSymbols.StockSymbolResponse
import com.ferelin.remote.api.webSocket.connector.WebSocketConnector
import com.ferelin.remote.api.webSocket.response.WebSocketResponse
import com.ferelin.remote.auth.AuthenticationManager
import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.database.RealtimeDatabase
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RemoteMediatorImpl @Inject constructor(
    private val mApiManager: ApiManager,
    private val mWebSocketConnector: WebSocketConnector,
    private val mAuthenticationManager: AuthenticationManager,
    private val mRealtimeDatabaseManager: RealtimeDatabase,
) : RemoteMediator {

    override fun openWebSocketConnection(): Flow<BaseResponse<WebSocketResponse>> {
        return mWebSocketConnector.openWebSocketConnection()
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

    override fun loadStockHistory(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): BaseResponse<StockHistoryResponse> {
        return mApiManager.loadStockHistory(symbol, from, to, resolution)
    }

    override fun loadCompanyProfile(symbol: String): BaseResponse<CompanyProfileResponse> {
        return mApiManager.loadCompanyProfile(symbol)
    }

    override fun loadStockSymbols(): BaseResponse<StockSymbolResponse> {
        return mApiManager.loadStockSymbols()
    }

    override fun loadCompanyNews(
        symbol: String,
        from: String,
        to: String
    ): BaseResponse<List<CompanyNewsResponse>> {
        return mApiManager.loadCompanyNews(symbol, from, to)
    }

    override fun sendRequestToLoadPrice(
        symbol: String,
        position: Int,
        isImportant: Boolean
    ) {
        return mApiManager.sendRequestToLoadPrice(symbol, position, isImportant)
    }

    override fun getStockPriceResponseState(): Flow<BaseResponse<StockPriceResponse>> {
        return mApiManager.getStockPriceResponseState()
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

    override fun eraseCompanyIdFromRealtimeDb(userToken: String, companyId: String) {
        mRealtimeDatabaseManager.eraseCompanyIdFromRealtimeDb(userToken, companyId)
    }

    override fun cacheCompanyIdToRealtimeDb(userToken: String, companyId: String) {
        mRealtimeDatabaseManager.cacheCompanyIdToRealtimeDb(userToken, companyId)
    }

    override fun getCompaniesIdsFromDb(userToken: String): Flow<BaseResponse<List<String>>> {
        return mRealtimeDatabaseManager.getCompaniesIdsFromDb(userToken)
    }

    override fun cacheSearchRequestToDb(
        userToken: String,
        searchRequestId: String,
        searchRequest: String
    ) {
        mRealtimeDatabaseManager.cacheSearchRequestToDb(userToken, searchRequestId, searchRequest)
    }

    override fun getSearchRequestsFromDb(userToken: String): Flow<BaseResponse<HashMap<Int, String>>> {
        return mRealtimeDatabaseManager.getSearchRequestsFromDb(userToken)
    }

    override fun eraseSearchRequestFromDb(userToken: String, searchRequestId: String) {
        mRealtimeDatabaseManager.eraseSearchRequestFromDb(userToken, searchRequestId)
    }

    override fun cacheChat(
        chatId: String,
        currentUserNumber: String,
        associatedUserNumber: String
    ) {
        mRealtimeDatabaseManager.cacheChat(chatId, currentUserNumber, associatedUserNumber)
    }

    override fun getChatsByUserNumber(userNumber: String): Flow<BaseResponse<String>> {
        return mRealtimeDatabaseManager.getChatsByUserNumber(userNumber)
    }

    override fun getMessagesForChat(
        currentUserNumber: String,
        associatedUserNumber: String
    ): Flow<BaseResponse<HashMap<String, Any>>> {
        return mRealtimeDatabaseManager.getMessagesForChat(currentUserNumber, associatedUserNumber)
    }

    override fun cacheMessage(
        messageId: String,
        currentUserNumber: String,
        associatedUserNumber: String,
        messageText: String,
        messageSideKey: Char
    ) {
        mRealtimeDatabaseManager.cacheMessage(
            messageId,
            currentUserNumber,
            associatedUserNumber,
            messageText,
            messageSideKey
        )
    }
}