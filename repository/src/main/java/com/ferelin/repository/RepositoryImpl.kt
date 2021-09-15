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

package com.ferelin.repository

import android.app.Activity
import com.ferelin.local.LocalManager
import com.ferelin.remote.RemoteMediator
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.converter.ConverterMediator
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.shared.MessageSide
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
        return mConverterMediator.convertLocalCompaniesResponseToRepositoryResponse(localCompanies)
    }

    override suspend fun cacheCompanyToLocalDb(adaptiveCompany: AdaptiveCompany) {
        val preparedForInsert = mConverterMediator.convertAdaptiveCompanyToCompany(adaptiveCompany)
        mLocalManager.updateCompany(preparedForInsert)
    }

    override fun eraseCompanyIdFromRealtimeDb(userToken: String, companyId: String) {
        mRemoteMediator.eraseCompanyIdFromRealtimeDb(userToken, companyId)
    }

    override fun cacheCompanyIdToRealtimeDb(userToken: String, companyId: String) {
        mRemoteMediator.cacheCompanyIdToRealtimeDb(userToken, companyId)
    }

    override suspend fun getCompaniesIdsFromRealtimeDb(
        userToken: String
    ): RepositoryResponse<List<String>> {
        val remoteResponse = mRemoteMediator.getCompaniesIdsFromDb(userToken).firstOrNull()
        return mConverterMediator.convertNetworkCompaniesResponseToRepositoryResponse(remoteResponse)
    }

    override suspend fun clearSearchRequestsLocalDb() {
        mLocalManager.clearSearchRequests()
    }

    override suspend fun getSearchRequestsFromLocalDb(): List<AdaptiveSearchRequest> {
        val searchesHistory = mLocalManager.getAllSearchRequests()
        return mConverterMediator.convertLocalRequestsResponseToAdaptiveRequests(searchesHistory)
    }

    override suspend fun cacheSearchRequestToLocalDb(searchRequest: AdaptiveSearchRequest) {
        val preparedForLocal = mConverterMediator.convertAdaptiveRequestToRequest(searchRequest)
        mLocalManager.cacheSearchRequest(preparedForLocal)
    }

    override suspend fun eraseSearchRequestFromLocalDb(searchRequest: AdaptiveSearchRequest) {
        val preparedForLocal = mConverterMediator.convertAdaptiveRequestToRequest(searchRequest)
        mLocalManager.eraseSearchRequest(preparedForLocal)
    }

    override suspend fun getSearchRequestsFromRealtimeDb(
        userToken: String
    ): RepositoryResponse<List<AdaptiveSearchRequest>> {
        val remoteResponse = mRemoteMediator.getSearchRequestsFromDb(userToken).firstOrNull()
        return mConverterMediator.convertNetworkRequestsResponseToRepositoryResponse(remoteResponse)
    }

    override fun cacheSearchRequestToRealtimeDb(
        userToken: String,
        searchRequest: AdaptiveSearchRequest
    ) {
        mRemoteMediator.cacheSearchRequestToDb(
            userToken = userToken,
            searchRequestId = searchRequest.id.toString(),
            searchRequest = searchRequest.searchText
        )
    }

    override fun eraseSearchRequestFromRealtimeDb(
        userToken: String,
        searchRequest: AdaptiveSearchRequest
    ) {
        mRemoteMediator.eraseSearchRequestFromDb(
            userToken = userToken,
            searchRequestId = searchRequest.id.toString()
        )
    }

    override fun clearChatsLocalDb() {
        mLocalManager.clearChats()
    }

    override suspend fun getFirstTimeLaunchState(): Boolean {
        return mLocalManager.getFirstTimeLaunchState() ?: true
    }

    override suspend fun cacheFirstTimeLaunchState(state: Boolean) {
        mLocalManager.cacheFirstTimeLaunchState(state)
    }

    override suspend fun cacheUserNumber(number: String) {
        mLocalManager.cacheUserNumber(number)
    }

    override suspend fun getUserNumber(): String {
        return mLocalManager.getUserNumber() ?: ""
    }

    override fun tryToSignIn(
        holderActivity: Activity,
        phone: String
    ): Flow<RepositoryResponse<RepositoryMessages>> {
        return mRemoteMediator.tryToLogIn(holderActivity, phone).map {
            mConverterMediator.convertAuthenticationResponseToRepositoryResponse(it)
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
        mLocalManager.clearMessages()
    }

    override fun loadStockHistory(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): RepositoryResponse<StockHistory> {
        val remoteResponse = mRemoteMediator.loadStockHistory(symbol, from, to, resolution)
        return mConverterMediator.convertApiResponseToAdaptiveStockCandles(remoteResponse, symbol)
    }

    override fun loadCompanyProfile(symbol: String): RepositoryResponse<CompanyProfile> {
        val remoteResponse = mRemoteMediator.loadCompanyProfile(symbol)
        return mConverterMediator.convertApiResponseToAdaptiveCompanyProfile(
            remoteResponse,
            symbol
        )
    }

    override fun loadStockSymbols(): RepositoryResponse<AdaptiveStocksSymbols> {
        val remoteResponse = mRemoteMediator.loadStockSymbols()
        return mConverterMediator.convertApiResponseToAdaptiveStockSymbols(remoteResponse)
    }

    override fun loadCompanyNews(
        symbol: String,
        from: String,
        to: String
    ): RepositoryResponse<CompanyNews> {
        val remoteResponse = mRemoteMediator.loadCompanyNews(symbol, from, to)
        return mConverterMediator.convertApiResponseToAdaptiveCompanyNews(remoteResponse, symbol)
    }

    override fun sendRequestToLoadPrice(
        symbol: String,
        position: Int,
        isImportant: Boolean
    ) {
        return mRemoteMediator.sendRequestToLoadPrice(symbol, position, isImportant)
    }

    override fun getStockPriceResponseState(): Flow<RepositoryResponse<StockPrice>> {
        return mRemoteMediator.getStockPriceResponseState().map { response ->
            mConverterMediator.convertApiResponseToAdaptiveCompanyDayData(response)
        }
    }

    override fun openWebSocketConnection(): Flow<RepositoryResponse<LiveTimePrice>> {
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
        val preparedForInsert = mConverterMediator.convertAdaptiveChatToChat(chat)
        mLocalManager.cacheChat(preparedForInsert)
    }

    override suspend fun getAllChatsFromLocalDb(): RepositoryResponse<List<AdaptiveChat>> {
        val localChats = mLocalManager.getAllChats()
        return mConverterMediator.convertChatsToAdaptiveChats(localChats)
    }

    override suspend fun cacheMessageToLocalDb(message: AdaptiveMessage) {
        val preparedForLocal = mConverterMediator.convertAdaptiveMessageToMessage(message)
        mLocalManager.cacheMessage(preparedForLocal)
    }

    override suspend fun getMessagesFromLocalDb(
        associatedUserNumber: String
    ): RepositoryResponse<List<AdaptiveMessage>> {
        val localMessages = mLocalManager.getMessages(associatedUserNumber)
        return mConverterMediator.convertLocalMessagesResponseToRepositoryResponse(localMessages)
    }

    override fun cacheChatToRealtimeDb(currentUserNumber: String, chat: AdaptiveChat) {
        mRemoteMediator.cacheChat(
            chatId = chat.id.toString(),
            currentUserNumber = currentUserNumber,
            associatedUserNumber = chat.associatedUserNumber
        )
    }

    override suspend fun getUserChatsFromRealtimeDb(
        userNumber: String
    ): Flow<RepositoryResponse<AdaptiveChat>> {
        return mRemoteMediator.getChatsByUserNumber(userNumber).map { response ->
            mConverterMediator.convertChatResponseToRepositoryResponse(response)
        }
    }

    override fun getMessagesFromRealtimeDb(
        currentUserNumber: String,
        associatedUserNumber: String
    ): Flow<RepositoryResponse<AdaptiveMessage>> {
        return mRemoteMediator.getMessagesForChat(currentUserNumber, associatedUserNumber)
            .map { response ->
                mConverterMediator.convertNetworkMessagesResponseToRepositoryResponse(response)
            }
    }

    override fun cacheNewMessageToRealtimeDb(currentUserNumber: String, message: AdaptiveMessage) {
        val messageId = message.id.toString()
        mRemoteMediator.cacheMessage(
            messageId = messageId,
            currentUserNumber = currentUserNumber,
            associatedUserNumber = message.associatedUserNumber,
            messageText = message.text,
            messageSideKey = message.side.key
        )

        mRemoteMediator.cacheMessage(
            messageId = messageId,
            currentUserNumber = message.associatedUserNumber,
            associatedUserNumber = currentUserNumber,
            messageText = message.text,
            messageSideKey = if (message.side is MessageSide.Source) {
                MessageSide.Associated.key
            } else MessageSide.Source.key
        )
    }
}