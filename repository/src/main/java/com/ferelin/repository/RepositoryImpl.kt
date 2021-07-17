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
import kotlinx.coroutines.flow.firstOrNull
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

    override suspend fun getCompaniesIdsFromRealtimeDb(
        userId: String
    ): RepositoryResponse<List<String>> {
        val remoteResponse = mRemoteMediator.readCompaniesIdsFromDb(userId).firstOrNull()
        return mConverterMediator.convertCompaniesIdsForUi(remoteResponse)
    }

    override suspend fun clearLocalSearchRequestsHistory() {
        mLocalManager.clearSearchRequestsHistory()
    }

    override suspend fun getSearchesHistoryFromLocalDb(): RepositoryResponse<List<AdaptiveSearchRequest>> {
        val searchesHistory = mLocalManager.getSearchesHistoryAsResponse()
        return mConverterMediator.convertSearchRequestsForUi(searchesHistory)
    }

    override suspend fun cacheSearchRequestsHistoryToLocalDb(requests: List<AdaptiveSearchRequest>) {
        val preparedForInsert = mConverterMediator.convertSearchRequestsForLocal(requests)
        mLocalManager.setSearchRequestsHistory(preparedForInsert)
    }

    override suspend fun getSearchRequestsFromRealtimeDb(
        userId: String
    ): RepositoryResponse<List<String>> {
        val remoteResponse = mRemoteMediator.readSearchRequestsFromDb(userId).firstOrNull()
        return mConverterMediator.convertSearchRequestsTextForUi(remoteResponse)
    }

    override fun cacheSearchRequestToRealtimeDb(
        userId: String,
        searchRequest: AdaptiveSearchRequest
    ) {
        mRemoteMediator.writeSearchRequestToDb(
            userId = userId,
            searchRequestId = searchRequest.id.toString(),
            searchRequest = searchRequest.searchText
        )
    }

    override fun eraseSearchRequestFromRealtimeDb(
        userId: String,
        searchRequest: AdaptiveSearchRequest
    ) {
        mRemoteMediator.eraseSearchRequestFromDb(
            userId = userId,
            searchRequestId = searchRequest.id.toString()
        )
    }

    override fun clearChatsLocalDb() {
        mLocalManager.clearChats()
    }

    override suspend fun getFirstTimeLaunchState(): Boolean {
        return mLocalManager.getFirstTimeLaunchState() == true
    }

    override suspend fun setFirstTimeLaunchState(state: Boolean) {
        mLocalManager.setFirstTimeLaunchState(state)
    }

    override suspend fun setUserNumber(number: String) {
        mLocalManager.setUserNumber(number)
    }

    override suspend fun getUserNumber(): String? {
        return mLocalManager.getUserNumber()
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

    override fun loadStockHistory(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): RepositoryResponse<AdaptiveCompanyHistory> {
        val remoteResponse = mRemoteMediator.loadStockHistory(symbol, from, to, resolution)
        return mConverterMediator.fromNetworkResponseToAdaptiveStockCandles(remoteResponse, symbol)
    }

    override fun loadCompanyProfile(symbol: String): RepositoryResponse<AdaptiveCompanyProfile> {
        val remoteResponse = mRemoteMediator.loadCompanyProfile(symbol)
        return mConverterMediator.fromNetworkResponseToAdaptiveCompanyProfile(
            remoteResponse,
            symbol
        )
    }

    override fun loadStockSymbols(): RepositoryResponse<AdaptiveStocksSymbols> {
        val remoteResponse = mRemoteMediator.loadStockSymbols()
        return mConverterMediator.fromNetworkResponseToAdaptiveStockSymbols(remoteResponse)
    }

    override fun loadCompanyNews(
        symbol: String,
        from: String,
        to: String
    ): RepositoryResponse<AdaptiveCompanyNews> {
        val remoteResponse = mRemoteMediator.loadCompanyNews(symbol, from, to)
        return mConverterMediator.fromNetworkResponseToAdaptiveCompanyNews(remoteResponse, symbol)
    }

    override fun loadStockPrice(
        symbol: String,
        position: Int,
        isImportant: Boolean
    ): Flow<RepositoryResponse<AdaptiveCompanyDayData>> {
        return mRemoteMediator.loadStockPrice(symbol, position, isImportant).map { response ->
            mConverterMediator.fromNetworkResponseToAdaptiveCompanyDayData(response)
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

    override fun cacheChatToRealtimeDb(currentUserNumber: String, chat: AdaptiveChat) {
        mRemoteMediator.cacheChat(
            id = chat.id.toString(),
            currentUserNumber = currentUserNumber,
            associatedUserNumber = chat.associatedUserNumber
        )
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

    override fun cacheNewMessageToRealtimeDb(currentUserNumber: String, message: AdaptiveMessage) {
        mRemoteMediator.cacheMessage(
            id = message.id.toString(),
            currentUserNumber = currentUserNumber,
            associatedUserNumber = message.associatedUserNumber,
            messageText = message.text,
            messageSideKey = message.side.key
        )
    }
}