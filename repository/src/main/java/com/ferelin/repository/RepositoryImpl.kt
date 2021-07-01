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
import com.ferelin.repository.converter.ConverterMediator
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
    private val mConverterMediator: ConverterMediator
) : Repository {

    override suspend fun getAllCompaniesFromLocalDb(): RepositoryResponse<List<AdaptiveCompany>> {
        val localCompanies = mLocalManager.getAllCompaniesAsResponse()
        return mConverterMediator.convertCompaniesResponseForUi(localCompanies)
    }

    override suspend fun cacheCompanyToLocalDb(adaptiveCompany: AdaptiveCompany) {
        val preparedForInsert = mConverterMediator.convertCompanyForLocal(adaptiveCompany)
        mLocalManager.updateCompany(preparedForInsert)
    }

    override fun eraseCompanyIdFromRealtimeDb(userId: String, companyId: String) {
        mRemoteMediator.eraseCompanyIdFromRealtimeDb(userId, companyId)
    }

    override fun cacheCompanyIdToRealtimeDb(userId: String, companyId: String) {
        mRemoteMediator.writeCompanyIdToRealtimeDb(userId, companyId)
    }

    override fun getCompaniesIdsFromRealtimeDb(userId: String): Flow<RepositoryResponse<String?>> {
        return mRemoteMediator.readCompaniesIdsFromDb(userId).map { response ->
            mConverterMediator.convertRealtimeDatabaseResponseForUi(response)
        }
    }

    override suspend fun clearLocalSearchRequestsHistory() {
        mLocalManager.clearSearchRequestsHistory()
    }

    override suspend fun getSearchesHistoryFromLocalDb(): RepositoryResponse<List<AdaptiveSearchRequest>> {
        val searchesHistory = mLocalManager.getSearchesHistoryAsResponse()
        return mConverterMediator.convertSearchesForUi(searchesHistory)
    }

    override suspend fun cacheSearchRequestsHistoryToLocalDb(requests: List<AdaptiveSearchRequest>) {
        val preparedForInsert = mConverterMediator.convertSearchesForLocal(requests)
        mLocalManager.setSearchRequestsHistory(preparedForInsert)
    }

    override fun getSearchRequestsFromRealtimeDb(userId: String): Flow<RepositoryResponse<String?>> {
        return mRemoteMediator.readSearchRequestsFromDb(userId).map { response ->
            mConverterMediator.convertRealtimeDatabaseResponseForUi(response)
        }
    }

    override fun eraseSearchRequestFromRealtimeDb(userId: String, searchRequest: String) {
        mRemoteMediator.eraseSearchRequestFromDb(userId, searchRequest)
    }

    override fun cacheSearchRequestToRealtimeDb(userId: String, searchRequest: String) {
        mRemoteMediator.writeSearchRequestToDb(userId, searchRequest)
    }

    override fun clearChatsLocalDb() {
        mLocalManager.clearChats()
    }

    override suspend fun getFirstTimeLaunchState(): RepositoryResponse<Boolean> {
        val firstTimeLaunch = mLocalManager.getFirstTimeLaunchState()
        return mConverterMediator.convertFirstTimeLaunchStateForUi(firstTimeLaunch)
    }

    override suspend fun setFirstTimeLaunchState(state: Boolean) {
        mLocalManager.setFirstTimeLaunchState(state)
    }

    override suspend fun getUserRegisterState(): Boolean? {
        return mLocalManager.getUserRegisterState()
    }

    override suspend fun setUserRegisterState(state: Boolean) {
        mLocalManager.setUserRegisterState(state)
    }

    override suspend fun setUserLogin(login: String) {
        mLocalManager.setUserLogin(login)
    }

    override suspend fun getUserLogin(): String? {
        return mLocalManager.getUserLogin()
    }

    override fun tryToSignIn(
        holderActivity: Activity,
        phone: String
    ): Flow<RepositoryResponse<RepositoryMessages>> {
        return mRemoteMediator.tryToLogIn(holderActivity, phone).map {
            mConverterMediator.convertAuthenticationResponseForUi(it)
        }
    }

    override fun logInWithCode(code: String) {
        mRemoteMediator.logInWithCode(code)
    }

    override fun logOut() {
        mRemoteMediator.logOut()
    }

    override fun getUserAuthenticationId(): String? {
        return mRemoteMediator.provideUserId()
    }

    override fun isUserAuthenticated(): Boolean {
        return mRemoteMediator.provideIsUserLogged()
    }

    override fun clearMessagesDatabase() {
        mLocalManager.clearMessagesTable()
    }

    override fun loadStockCandles(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): Flow<RepositoryResponse<AdaptiveCompanyHistory>> {
        return mRemoteMediator.loadStockCandles(symbol, from, to, resolution).map {
            mConverterMediator.convertStockCandlesResponseForUi(it, symbol)
        }
    }

    override fun loadCompanyProfile(symbol: String): Flow<RepositoryResponse<AdaptiveCompanyProfile>> {
        return mRemoteMediator.loadCompanyProfile(symbol).map {
            mConverterMediator.convertCompanyProfileResponseForUi(it, symbol)
        }
    }

    override fun loadStockSymbols(): Flow<RepositoryResponse<AdaptiveStocksSymbols>> {
        return mRemoteMediator.loadStockSymbols().map {
            mConverterMediator.convertStockSymbolsResponseForUi(it)
        }
    }

    override fun loadCompanyNews(
        symbol: String,
        from: String,
        to: String
    ): Flow<RepositoryResponse<AdaptiveCompanyNews>> {
        return mRemoteMediator.loadCompanyNews(symbol, from, to).map {
            mConverterMediator.convertCompanyNewsResponseForUi(it, symbol)
        }
    }

    override fun loadCompanyQuote(
        symbol: String,
        position: Int,
        isImportant: Boolean
    ): Flow<RepositoryResponse<AdaptiveCompanyDayData>> {
        return mRemoteMediator.loadCompanyQuote(symbol, position, isImportant).map {
            mConverterMediator.convertCompanyQuoteResponseForUi(it)
        }
    }

    override fun openWebSocketConnection(): Flow<RepositoryResponse<AdaptiveWebSocketPrice>> {
        return mRemoteMediator.openWebSocketConnection().map {
            mConverterMediator.convertWebSocketResponseForUi(it)
        }
    }

    override fun closeWebSocketConnection() {
        mRemoteMediator.closeWebSocketConnection()
    }

    override fun subscribeItemOnLiveTimeUpdates(symbol: String, previousPrice: Double) {
        mRemoteMediator.subscribeItemOnLiveTimeUpdates(symbol, previousPrice)
    }

    override fun unsubscribeItemFromLiveTimeUpdates(symbol: String) {
        mRemoteMediator.unsubscribeItemFromLiveTimeUpdates(symbol)
    }

    override suspend fun cacheChatToLocalDb(chat: AdaptiveChat) {
        val preparedForInsert = mConverterMediator.convertAdaptiveChatForLocal(chat)
        mLocalManager.insertChat(preparedForInsert)
    }

    override suspend fun getAllChatsFromLocalDb(): RepositoryResponse<List<AdaptiveChat>> {
        val localChats = mLocalManager.getAllChats()
        return mConverterMediator.convertLocalChatsForUi(localChats)
    }

    override suspend fun cacheMessageToLocalDb(message: AdaptiveMessage) {
        val preparedForLocal = mConverterMediator.convertMessageForLocal(message)
        mLocalManager.insertMessage(preparedForLocal)
    }

    override suspend fun getMessagesFromLocalDb(
        associatedUserNumber: String
    ): RepositoryResponse<List<AdaptiveMessage>> {
        val localMessages = mLocalManager.getMessages(associatedUserNumber)
        return mConverterMediator.convertLocalMessagesResponseForUi(localMessages)
    }

    override fun cacheChatToRealtimeDb(sourceUserNumber: String, secondSideUserNumber: String) {
        mRemoteMediator.cacheChat(sourceUserNumber, secondSideUserNumber)
    }

    override suspend fun getUserChatsFromRealtimeDb(
        userNumber: String
    ): Flow<RepositoryResponse<AdaptiveChat>> {
        return mRemoteMediator.getUserChats(userNumber).map { response ->
            mConverterMediator.convertRemoteChatResponseForUi(response)
        }
    }

    override fun getMessagesFromRealtimeDb(
        currentUserNumber: String,
        associatedUserNumber: String
    ): Flow<RepositoryResponse<AdaptiveMessage>> {
        return mRemoteMediator.getMessagesForChat(currentUserNumber, associatedUserNumber)
            .map { response -> mConverterMediator.convertRemoteMessageResponseForUi(response) }
    }

    override fun cacheNewMessageToRealtimeDb(
        currentUserNumber: String,
        associatedUserNumber: String,
        messageText: String,
        messageSideKey: Char
    ) {
        mRemoteMediator.cacheMessage(
            currentUserNumber,
            associatedUserNumber,
            messageText,
            messageSideKey
        )
    }
}