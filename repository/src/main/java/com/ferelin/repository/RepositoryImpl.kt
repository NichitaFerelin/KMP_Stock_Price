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
import com.ferelin.repository.converter.ResponseMediator
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
    private val mResponseMediator: ResponseMediator
) : Repository {

    override suspend fun getAllCompaniesFromLocalDb(): RepositoryResponse<List<AdaptiveCompany>> {
        val localCompanies = mLocalManager.getAllCompaniesAsResponse()
        return mResponseMediator.convertCompaniesResponseForUi(localCompanies)
    }

    override suspend fun cacheCompanyToLocalDb(adaptiveCompany: AdaptiveCompany) {
        val preparedForInsert = mResponseMediator.convertCompanyForLocal(adaptiveCompany)
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
            mResponseMediator.convertRealtimeDatabaseResponseForUi(response)
        }
    }

    override suspend fun clearLocalSearchRequestsHistory() {
        mLocalManager.clearSearchRequestsHistory()
    }

    override suspend fun getSearchesHistoryFromLocalDb(): RepositoryResponse<List<AdaptiveSearchRequest>> {
        val searchesHistory = mLocalManager.getSearchesHistoryAsResponse()
        return mResponseMediator.convertSearchesForUi(searchesHistory)
    }

    override suspend fun cacheSearchRequestsHistoryToLocalDb(requests: List<AdaptiveSearchRequest>) {
        val preparedForInsert = mResponseMediator.convertSearchesForLocal(requests)
        mLocalManager.setSearchRequestsHistory(preparedForInsert)
    }

    override fun getSearchRequestsFromRealtimeDb(userId: String): Flow<RepositoryResponse<String?>> {
        return mRemoteMediator.readSearchRequestsFromDb(userId).map { response ->
            mResponseMediator.convertRealtimeDatabaseResponseForUi(response)
        }
    }

    override fun eraseSearchRequestFromRealtimeDb(userId: String, searchRequest: String) {
        mRemoteMediator.eraseSearchRequestFromDb(userId, searchRequest)
    }

    override fun cacheSearchRequestToRealtimeDb(userId: String, searchRequest: String) {
        mRemoteMediator.writeSearchRequestToDb(userId, searchRequest)
    }

    override fun cacheNewRelationToRealtimeDb(
        sourceUserLogin: String,
        secondSideUserLogin: String,
        relationId: String
    ) {
        mRemoteMediator.addNewRelation(sourceUserLogin, secondSideUserLogin, relationId)
    }

    override suspend fun getUserRelationsFromRealtimeDb(
        userLogin: String
    ): RepositoryResponse<List<AdaptiveRelation>> {
        val response = mRemoteMediator.getUserRelations(userLogin).firstOrNull()
        return mResponseMediator.convertRealtimeRelationResponseForUi(response)
    }

    override suspend fun eraseRelationFromLocalDb(relation: AdaptiveRelation) {
        val preparedForLocal = mResponseMediator.convertRelationForLocal(relation)
        mLocalManager.deleteRelation(preparedForLocal)
    }

    override fun eraseRelationFromRealtimeDb(sourceUserLogin: String, relationId: String) {
        mRemoteMediator.eraseRelation(sourceUserLogin, relationId)
    }

    override suspend fun cacheRelationToLocalDb(relation: AdaptiveRelation) {
        val preparedForInsert = mResponseMediator.convertRelationForLocal(relation)
        mLocalManager.insertRelation(preparedForInsert)
    }

    override suspend fun getAllRelationsFromLocalDb(): RepositoryResponse<List<AdaptiveRelation>> {
        val localResponse = mLocalManager.getAllRelations()
        return mResponseMediator.convertLocalRelationResponseForUi(localResponse)
    }

    override fun clearRelationsDatabase() {
        mLocalManager.clearRelationsTable()
    }

    override suspend fun getFirstTimeLaunchState(): RepositoryResponse<Boolean> {
        val firstTimeLaunch = mLocalManager.getFirstTimeLaunchState()
        return mResponseMediator.convertFirstTimeLaunchStateForUi(firstTimeLaunch)
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

    override suspend fun isUserExist(login: String): Boolean {
        return mRemoteMediator.findUserByLogin(login).firstOrNull() == true
    }

    override suspend fun isUserIdExist(userId: String): Boolean {
        return mRemoteMediator.findUserById(userId).firstOrNull() == true
    }

    override suspend fun tryToRegister(
        userId: String,
        login: String
    ): Flow<RepositoryResponse<Boolean>> {
        return mRemoteMediator.tryToRegister(userId, login).map { response ->
            mResponseMediator.convertTryToRegisterResponseForUi(response)
        }
    }

    override fun tryToSignIn(
        holderActivity: Activity,
        phone: String
    ): Flow<RepositoryResponse<RepositoryMessages>> {
        return mRemoteMediator.tryToLogIn(holderActivity, phone).map {
            mResponseMediator.convertAuthenticationResponseForUi(it)
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

    override suspend fun cacheMessageToLocalDb(messagesHolder: AdaptiveMessagesHolder) {
        val preparedForInsert = mResponseMediator.convertMessageForLocal(messagesHolder)
        mLocalManager.insertMessage(preparedForInsert)
    }

    override suspend fun getAllMessagesFromLocalDb(): RepositoryResponse<List<AdaptiveMessagesHolder>> {
        val localMessages = mLocalManager.getAllMessages()
        return mResponseMediator.convertLocalMessagesResponseForUi(localMessages)
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
            mResponseMediator.convertStockCandlesResponseForUi(it, symbol)
        }
    }

    override fun loadCompanyProfile(symbol: String): Flow<RepositoryResponse<AdaptiveCompanyProfile>> {
        return mRemoteMediator.loadCompanyProfile(symbol).map {
            mResponseMediator.convertCompanyProfileResponseForUi(it, symbol)
        }
    }

    override fun loadStockSymbols(): Flow<RepositoryResponse<AdaptiveStocksSymbols>> {
        return mRemoteMediator.loadStockSymbols().map {
            mResponseMediator.convertStockSymbolsResponseForUi(it)
        }
    }

    override fun loadCompanyNews(
        symbol: String,
        from: String,
        to: String
    ): Flow<RepositoryResponse<AdaptiveCompanyNews>> {
        return mRemoteMediator.loadCompanyNews(symbol, from, to).map {
            mResponseMediator.convertCompanyNewsResponseForUi(it, symbol)
        }
    }

    override fun loadCompanyQuote(
        symbol: String,
        position: Int,
        isImportant: Boolean
    ): Flow<RepositoryResponse<AdaptiveCompanyDayData>> {
        return mRemoteMediator.loadCompanyQuote(symbol, position, isImportant).map {
            mResponseMediator.convertCompanyQuoteResponseForUi(it)
        }
    }

    override fun openWebSocketConnection(): Flow<RepositoryResponse<AdaptiveWebSocketPrice>> {
        return mRemoteMediator.openWebSocketConnection().map {
            mResponseMediator.convertWebSocketResponseForUi(it)
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

    override fun getMessagesAssociatedWithSpecifiedUserFromRealtimeDb(
        sourceUserLogin: String,
        secondSideUserLogin: String
    ): Flow<RepositoryResponse<AdaptiveMessagesHolder>> {
        return mRemoteMediator.getMessagesAssociatedWithSpecifiedUser(
            sourceUserLogin,
            secondSideUserLogin
        ).map { response -> mResponseMediator.convertRemoteMessagesResponseForUi(response) }
    }

    override fun cacheNewMessageToRealtimeDb(
        sourceUserLogin: String,
        secondSideUserLogin: String,
        messageId: String,
        message: String,
        side: MessageSide
    ) {
        mRemoteMediator.addNewMessage(
            sourceUserLogin,
            secondSideUserLogin,
            messageId,
            message,
            side
        )
    }
}