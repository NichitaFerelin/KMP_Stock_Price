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
import com.ferelin.stockprice.common.menu.MenuItem
import com.ferelin.stockprice.dataInteractor.dataManager.workers.authentication.AuthenticationWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.authentication.AuthenticationWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.companies.CompaniesMediator
import com.ferelin.stockprice.dataInteractor.dataManager.workers.companies.defaults.CompaniesWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.companies.favourites.FavouriteCompaniesWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.errors.ErrorsWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.errors.ErrorsWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.menuItems.MenuItemsWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.menuItems.MenuItemsWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.messages.MessagesWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.messages.MessagesWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.network.NetworkConnectivityWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.register.RegisterWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.relations.RelationsWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.relations.RelationsWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.searchRequests.SearchRequestsWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.searchRequests.SearchRequestsWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.webSocket.WebSocketWorker
import com.ferelin.stockprice.dataInteractor.syncManager.SynchronizationManager
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
    private val mSynchronizationManager: SynchronizationManager,
    private val mCompaniesMediator: CompaniesMediator,

    // Workers
    private val mSearchRequestsWorker: SearchRequestsWorker,
    private val mMenuItemsWorker: MenuItemsWorker,
    private val mErrorsWorker: ErrorsWorker,
    private val mMessagesWorker: MessagesWorker,
    private val mRelationsWorker: RelationsWorker,
    private val mAuthenticationWorker: AuthenticationWorker,
    private val mWebSocketWorker: WebSocketWorker,
    private val mRegisterWorker: RegisterWorker,

    // States
    private val mCompaniesWorkerStates: CompaniesWorkerStates,
    private val mFavouriteCompaniesWorkerStates: FavouriteCompaniesWorkerStates,
    private val mSearchRequestsWorkerStates: SearchRequestsWorkerStates,
    private val mNetworkConnectivityWorkerStates: NetworkConnectivityWorkerStates,
    private val mMenuItemsWorkerStates: MenuItemsWorkerStates,
    private val mMessagesWorkerStates: MessagesWorkerStates,
    private val mErrorsWorkerStates: ErrorsWorkerStates,
    private val mAuthenticationWorkerStates: AuthenticationWorkerStates,
    private val mRelationsWorkerStates: RelationsWorkerStates
) : DataInteractor {

    /**
     * Companies states
     * */
    override val stateCompanies: StateFlow<DataNotificator<ArrayList<AdaptiveCompany>>>
        get() = mCompaniesWorkerStates.stateCompanies

    override val sharedCompaniesUpdates: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mCompaniesWorkerStates.sharedCompaniesUpdates

    override val companies: List<AdaptiveCompany>
        get() = mCompaniesWorkerStates.companies

    /**
     * Favourite companies states
     * */
    override val stateFavouriteCompanies: StateFlow<DataNotificator<ArrayList<AdaptiveCompany>>>
        get() = mFavouriteCompaniesWorkerStates.stateFavouriteCompanies

    override val stateCompanyForObserver: StateFlow<AdaptiveCompany?>
        get() = mFavouriteCompaniesWorkerStates.stateCompanyForObserver

    override val sharedFavouriteCompaniesUpdates: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mFavouriteCompaniesWorkerStates.sharedFavouriteCompaniesUpdates

    /**
     * Search requests states
     * */
    override val stateSearchRequests: StateFlow<DataNotificator<ArrayList<AdaptiveSearchRequest>>>
        get() = mSearchRequestsWorkerStates.stateSearchRequests

    override val statePopularSearchRequests: StateFlow<DataNotificator<ArrayList<AdaptiveSearchRequest>>>
        get() = mSearchRequestsWorkerStates.statePopularSearchRequests

    override val searchRequests: List<AdaptiveSearchRequest>
        get() = mSearchRequestsWorkerStates.searchRequests

    override val stateIsNetworkAvailable: StateFlow<Boolean>
        get() = mNetworkConnectivityWorkerStates.stateIsNetworkAvailable

    override val stateMenuItems: StateFlow<DataNotificator<List<MenuItem>>>
        get() = mMenuItemsWorkerStates.stateMenuItems

    override val sharedLogOut: SharedFlow<Unit>
        get() = mMenuItemsWorkerStates.sharedLogOut

    /*
    * Errors states
    * */
    override val sharedLoadMessagesError: SharedFlow<String>
        get() = mErrorsWorkerStates.sharedLoadMessagesError

    override val sharedApiLimitError: SharedFlow<String>
        get() = mErrorsWorkerStates.sharedApiLimitError

    override val sharedPrepareCompaniesError: SharedFlow<String>
        get() = mErrorsWorkerStates.sharedPrepareCompaniesError

    override val sharedLoadStockCandlesError: SharedFlow<String>
        get() = mErrorsWorkerStates.sharedLoadStockCandlesError

    override val sharedLoadCompanyNewsError: SharedFlow<String>
        get() = mErrorsWorkerStates.sharedLoadCompanyNewsError

    override val sharedLoadSearchRequestsError: SharedFlow<String>
        get() = mErrorsWorkerStates.sharedLoadSearchRequestsError

    override val sharedFavouriteCompaniesLimitReached: SharedFlow<String>
        get() = mErrorsWorkerStates.sharedFavouriteCompaniesLimitReached

    override val sharedAuthenticationError: SharedFlow<String>
        get() = mErrorsWorkerStates.sharedAuthenticationError

    override val sharedRegisterError: SharedFlow<String>
        get() = mErrorsWorkerStates.sharedRegisterError

    /**
     * Relations states
     * */
    override val stateUserRelations: StateFlow<DataNotificator<List<AdaptiveRelation>>>
        get() = mRelationsWorkerStates.stateUserRelations

    override val sharedUserRelationsUpdates: SharedFlow<DataNotificator<AdaptiveRelation>>
        get() = mRelationsWorkerStates.sharedUserRelationsUpdates

    /**
     * Other states
     * */
    override val sharedMessagesHolderUpdates: SharedFlow<AdaptiveMessage>
        get() = mMessagesWorkerStates.sharedMessagesHolderUpdates

    override val stockHistoryConverter: StockHistoryConverter
        get() = StockHistoryConverter

    override val userLogin: String
        get() = mAuthenticationWorkerStates.userLogin ?: ""

    override suspend fun prepareData() {
        prepareCompaniesData()
        prepareSearchesHistory()
        prepareUserRelations()
    }

    override suspend fun loadStockCandlesFromNetwork(symbol: String): Flow<AdaptiveCompany> {
        return mCompaniesMediator.loadStockCandlesFromNetwork(
            symbol,
            onError = { message, companySymbol ->
                mErrorsWorker.onLoadStockCandlesError(message, companySymbol)
            })
    }

    override suspend fun loadCompanyNewsFromNetwork(symbol: String): Flow<AdaptiveCompany> {
        return mCompaniesMediator.loadCompanyNewsFromNetwork(
            symbol,
            onError = { message, companySymbol ->
                mErrorsWorker.onLoadCompanyNewsError(message, companySymbol)
            }
        )
    }

    override suspend fun loadCompanyQuoteFromNetwork(
        symbol: String,
        position: Int,
        isImportant: Boolean
    ): Flow<AdaptiveCompany> {
        return mCompaniesMediator.loadCompanyQuoteFromNetwork(symbol, position, isImportant)
    }

    override suspend fun signIn(holderActivity: Activity, phone: String): Flow<RepositoryMessages> {
        return mAuthenticationWorker.signIn(
            holderActivity,
            phone,
            onLogStateChanged = { logState -> mMenuItemsWorker.onLogStateChanged(logState) },
            onError = { message -> mErrorsWorker.onAuthenticationError(message) }
        )
    }

    override fun isUserLogged(): Boolean {
        return mAuthenticationWorker.isUserLogged()
    }

    override fun logInWithCode(code: String) {
        mAuthenticationWorker.logInWithCode(code)
    }

    override suspend fun logOut() {
        mAuthenticationWorker.logOut()
    }

    override suspend fun addCompanyToFavourites(company: AdaptiveCompany, ignoreError: Boolean) {
        mCompaniesMediator.addCompanyToFavourites(company, ignoreError)
    }

    override suspend fun removeCompanyFromFavourites(company: AdaptiveCompany) {
        mCompaniesMediator.removeCompanyFromFavourites(company)
    }

    override suspend fun tryToRegister(login: String): Flow<Boolean> {
        return mRegisterWorker.tryToRegister(
            login,
            onError = { message -> mErrorsWorker.onRegisterError(message) }
        )
    }

    override suspend fun isUserRegistered(): Boolean {
        return mRegisterWorker.isUserRegistered()
    }

    override suspend fun createNewRelation(sourceUserLogin: String, associatedUserLogin: String) {
        mRelationsWorker.createNewRelation(sourceUserLogin, associatedUserLogin)
    }

    override suspend fun removeRelation(sourceUserLogin: String, relation: AdaptiveRelation) {
        mRelationsWorker.removeRelation(sourceUserLogin, relation)
    }

    override suspend fun cacheNewSearchRequest(searchText: String) {
        mSearchRequestsWorker.cacheNewSearchRequest(searchText)
    }

    override suspend fun openWebSocketConnection(): Flow<AdaptiveCompany> {
        return mWebSocketWorker.openWebSocketConnection()
    }

    override suspend fun getMessagesStateForLoginFromCache(
        associatedUserLogin: String
    ): StateFlow<DataNotificator<AdaptiveMessagesHolder>> {
        return mMessagesWorker.getMessagesStateForLoginFromCache(associatedUserLogin)
    }

    override suspend fun loadMessagesAssociatedWithLogin(associatedLogin: String) {
        mMessagesWorker.loadMessagesAssociatedWithLogin(
            sourceUserLogin = userLogin,
            associatedLogin = associatedLogin,
            onError = { mErrorsWorker.onLoadMessageError() }
        )
    }

    override suspend fun sendNewMessage(associatedUserLogin: String, text: String) {
        mMessagesWorker.sendNewMessage(
            sourceUserLogin = userLogin,
            associatedUserLogin = associatedUserLogin,
            text = text
        )
    }

    override fun provideNetworkStateFlow(): Flow<Boolean> {
        return stateIsNetworkAvailable.onEach {
            mSynchronizationManager.onNetworkStateChanged(it)
        }
    }

    private suspend fun prepareCompaniesData() {
        val responseCompanies = mRepository.getAllCompaniesFromLocalDb()
        if (responseCompanies is RepositoryResponse.Success) {
            mCompaniesMediator.onCompaniesDataPrepared(responseCompanies.data)
        } else mErrorsWorker.onPrepareCompaniesError()
    }

    private suspend fun prepareSearchesHistory() {
        val responseSearchesHistory = mRepository.getSearchesHistoryFromLocalDb()
        if (responseSearchesHistory is RepositoryResponse.Success) {
            mSearchRequestsWorker.onSearchRequestsDataPrepared(responseSearchesHistory.data)
        } else mErrorsWorker.onLoadSearchRequestsError()
    }

    private suspend fun prepareUserRelations() {
        if (!mRepository.isUserAuthenticated()) {
            return
        }

        val relationsResponse = mRepository.getAllRelationsFromLocalDb()
        if (relationsResponse is RepositoryResponse.Success) {
            mRelationsWorker.onRelationsPrepared(relationsResponse.data)
        }
    }
}