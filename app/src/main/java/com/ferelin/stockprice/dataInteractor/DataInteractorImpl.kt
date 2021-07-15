package com.ferelin.stockprice.dataInteractor

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
import com.ferelin.repository.Repository
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.dataInteractor.workers.authentication.AuthenticationWorker
import com.ferelin.stockprice.dataInteractor.workers.chats.ChatsWorker
import com.ferelin.stockprice.dataInteractor.workers.companies.CompaniesMediator
import com.ferelin.stockprice.dataInteractor.workers.errors.ErrorsWorker
import com.ferelin.stockprice.dataInteractor.workers.menuItems.MenuItemsWorker
import com.ferelin.stockprice.dataInteractor.workers.messages.MessagesWorker
import com.ferelin.stockprice.dataInteractor.workers.network.NetworkConnectivityWorker
import com.ferelin.stockprice.dataInteractor.workers.searchRequests.SearchRequestsWorker
import com.ferelin.stockprice.dataInteractor.workers.webSocket.WebSocketWorker
import com.ferelin.stockprice.ui.bottomDrawerSection.utils.adapter.MenuItem
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.StockHistoryConverter
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [DataInteractorImpl] is MAIN and SINGLE entity for the UI layer interaction with data.
 *   - Providing states of data and errors.
 *   - Sending network requests to Repository using [mRepository].
 *   - Sending local requests to Repository using [mLocalInteractor].
 *   - Sending errors to [mErrorsWorker].
 *   - Providing states about data loading to [mDataMediator].
 */
@Singleton
class DataInteractorImpl @Inject constructor(
    private val mRepository: Repository,
    private val mCompaniesMediator: CompaniesMediator,

    // Workers
    private val mSearchRequestsWorker: SearchRequestsWorker,
    private val mMenuItemsWorker: MenuItemsWorker,
    private val mErrorsWorker: ErrorsWorker,
    private val mMessagesWorker: MessagesWorker,
    private val mChatsWorker: ChatsWorker,
    private val mAuthenticationWorker: AuthenticationWorker,
    private val mWebSocketWorker: WebSocketWorker,
    private val mNetworkConnectivityWorker: NetworkConnectivityWorker
) : DataInteractor {

    /**
     * Companies states
     * */
    override val stateCompanies: StateFlow<DataNotificator<List<AdaptiveCompany>>>
        get() = mCompaniesMediator.stateCompanies

    override val sharedCompaniesUpdates: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mCompaniesMediator.sharedCompaniesUpdates

    /**
     * Favourite companies states
     * */
    override val stateFavouriteCompanies: StateFlow<DataNotificator<List<AdaptiveCompany>>>
        get() = mCompaniesMediator.stateFavouriteCompanies

    override val stateCompanyForObserver: StateFlow<AdaptiveCompany?>
        get() = mCompaniesMediator.stateCompanyForObserver

    override val sharedFavouriteCompaniesUpdates: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mCompaniesMediator.sharedFavouriteCompaniesUpdates

    /**
     * Search requests states
     * */
    override val stateSearchRequests: StateFlow<DataNotificator<List<AdaptiveSearchRequest>>>
        get() = mSearchRequestsWorker.stateSearchRequests

    override val statePopularSearchRequests: StateFlow<DataNotificator<List<AdaptiveSearchRequest>>>
        get() = mSearchRequestsWorker.statePopularSearchRequests

    /**
     * Menu items states
     * */
    override val stateMenuItems: StateFlow<DataNotificator<List<MenuItem>>>
        get() = mMenuItemsWorker.stateMenuItems

    /**
     * Errors states
     * */
    override val sharedLoadMessagesError: SharedFlow<String>
        get() = mErrorsWorker.sharedLoadMessagesError

    override val sharedApiLimitError: SharedFlow<String>
        get() = mErrorsWorker.sharedApiLimitError

    override val sharedPrepareCompaniesError: SharedFlow<String>
        get() = mErrorsWorker.sharedPrepareCompaniesError

    override val sharedLoadStockCandlesError: SharedFlow<String>
        get() = mErrorsWorker.sharedLoadStockCandlesError

    override val sharedLoadCompanyNewsError: SharedFlow<String>
        get() = mErrorsWorker.sharedLoadCompanyNewsError

    override val sharedLoadSearchRequestsError: SharedFlow<String>
        get() = mErrorsWorker.sharedLoadSearchRequestsError

    override val sharedFavouriteCompaniesLimitReached: SharedFlow<String>
        get() = mErrorsWorker.sharedFavouriteCompaniesLimitReached

    override val sharedAuthenticationError: SharedFlow<String>
        get() = mErrorsWorker.sharedAuthenticationError

    override val sharedRegisterError: SharedFlow<String>
        get() = mErrorsWorker.sharedRegisterError

    /**
     * Chats states
     * */
    override val stateUserChats: StateFlow<DataNotificator<List<AdaptiveChat>>>
        get() = mChatsWorker.stateUserChats

    override val sharedUserChatUpdates: SharedFlow<DataNotificator<AdaptiveChat>>
        get() = mChatsWorker.sharedUserChatUpdates

    /**
     * Other states
     * */

    override val stateMessages: StateFlow<DataNotificator<ArrayList<AdaptiveMessage>>>
        get() = mMessagesWorker.stateMessages

    override val sharedMessagesHolderUpdates: SharedFlow<AdaptiveMessage>
        get() = mMessagesWorker.sharedMessagesHolderUpdates

    override val stateIsNetworkAvailable: StateFlow<Boolean>
        get() = mNetworkConnectivityWorker.stateIsNetworkAvailable

    override val stockHistoryConverter: StockHistoryConverter
        get() = StockHistoryConverter

    override fun provideNetworkStateFlow(): Flow<Boolean> {
        return mNetworkConnectivityWorker.stateIsNetworkAvailable
            .onEach { isAvailable ->
                if (isAvailable) {
                    mCompaniesMediator.onNetworkAvailable()
                    mSearchRequestsWorker.onNetworkAvailable()
                } else {
                    mCompaniesMediator.onNetworkLost()
                    mSearchRequestsWorker.onNetworkLost()
                }
            }
    }

    override suspend fun tryToSignIn(
        holderActivity: Activity,
        phone: String
    ): Flow<RepositoryMessages> {
        return mAuthenticationWorker.tryToSignIn(holderActivity, phone,
            onLogIn = {
                mMenuItemsWorker.onLogIn()
                mCompaniesMediator.onLogIn()
                mSearchRequestsWorker.onLogIn()
                mChatsWorker.onLogIn()
            },
            onError = { message -> mErrorsWorker.onAuthenticationError(message) }
        )
    }

    override fun logInWithCode(code: String) {
        mAuthenticationWorker.logInWithCode(code)
    }

    override suspend fun logOut() {
        mAuthenticationWorker.logOut()
    }

    override suspend fun addCompanyToFavourites(company: AdaptiveCompany, ignoreError: Boolean) {
        mCompaniesMediator.addCompanyToFavourites(company, false)
    }

    override suspend fun removeCompanyFromFavourites(company: AdaptiveCompany) {
        mCompaniesMediator.removeCompanyFromFavourites(company)
    }

    override suspend fun loadStockHistory(symbol: String) {
        mCompaniesMediator.loadStockHistory(symbol)
    }

    override suspend fun loadCompanyNews(symbol: String) {
        mCompaniesMediator.loadCompanyNews(symbol)
    }

    override suspend fun loadStockPrice(
        symbol: String,
        position: Int,
        isImportant: Boolean
    ) : Flow<RepositoryResponse<AdaptiveCompanyDayData>> {
        return mCompaniesMediator.loadStockPrice(symbol, position, isImportant)
    }

    override fun createNewChat(associatedUserNumber: String) {
        mChatsWorker.createNewChat(associatedUserNumber)
    }

    override suspend fun setFirstTimeLaunchState(state: Boolean) {
        mRepository.setFirstTimeLaunchState(state)
    }

    override suspend fun getFirstTimeLaunchState(): Boolean {
        return mRepository.getFirstTimeLaunchState()
    }

    override fun loadMessagesFor(associatedUserNumber: String) {
        mMessagesWorker.prepareMessagesFor(associatedUserNumber)
    }

    override suspend fun sendMessageTo(associatedUserNumber: String, messageText: String) {
        mMessagesWorker.sendMessageTo(associatedUserNumber, messageText)
    }

    override suspend fun cacheNewSearchRequest(searchText: String) {
        mSearchRequestsWorker.cacheNewSearchRequest(searchText)
    }

    override suspend fun getUserNumber(): String? {
        return mRepository.getUserNumber()
    }

    override suspend fun openWebSocketConnection(): Flow<RepositoryResponse<AdaptiveWebSocketPrice>> {
        return mWebSocketWorker.openWebSocketConnection()
    }

    override fun prepareForWebSocketReconnection() {
        mWebSocketWorker.prepareToWebSocketReconnection()
    }
}