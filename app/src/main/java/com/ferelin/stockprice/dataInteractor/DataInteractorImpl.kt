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
import com.ferelin.stockprice.dataInteractor.dataManager.workers.authentication.AuthenticationWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.companies.CompaniesMediator
import com.ferelin.stockprice.dataInteractor.dataManager.workers.errors.ErrorsWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.menuItems.MenuItemsWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.messages.MessagesWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.network.NetworkConnectivityWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.register.RegisterWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.relations.RelationsWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.searchRequests.SearchRequestsWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.webSocket.WebSocketWorker
import com.ferelin.stockprice.dataInteractor.syncManager.SynchronizationManager
import com.ferelin.stockprice.ui.bottomDrawerSection.menu.adapter.MenuItem
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
    private val mNetworkConnectivityWorker: NetworkConnectivityWorker
) : DataInteractor {

    /**
     * Companies states
     * */
    override val stateCompanies: StateFlow<DataNotificator<ArrayList<AdaptiveCompany>>>
        get() = mCompaniesMediator.stateCompanies

    override val sharedCompaniesUpdates: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mCompaniesMediator.sharedCompaniesUpdates

    override val companies: List<AdaptiveCompany>
        get() = mCompaniesMediator.companies

    override val favouriteCompanies: List<AdaptiveCompany>
        get() = mCompaniesMediator.favouriteCompanies

    /**
     * Favourite companies states
     * */
    override val stateFavouriteCompanies: StateFlow<DataNotificator<ArrayList<AdaptiveCompany>>>
        get() = mCompaniesMediator.stateFavouriteCompanies

    override val stateCompanyForObserver: StateFlow<AdaptiveCompany?>
        get() = mCompaniesMediator.stateCompanyForObserver

    override val sharedFavouriteCompaniesUpdates: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mCompaniesMediator.sharedFavouriteCompaniesUpdates

    /**
     * Search requests states
     * */
    override val stateSearchRequests: StateFlow<DataNotificator<ArrayList<AdaptiveSearchRequest>>>
        get() = mSearchRequestsWorker.stateSearchRequests

    override val statePopularSearchRequests: StateFlow<DataNotificator<ArrayList<AdaptiveSearchRequest>>>
        get() = mSearchRequestsWorker.statePopularSearchRequests

    override val searchRequests: List<AdaptiveSearchRequest>
        get() = mSearchRequestsWorker.searchRequests

    override val stateIsNetworkAvailable: StateFlow<Boolean>
        get() = mNetworkConnectivityWorker.stateIsNetworkAvailable

    override val stateMenuItems: StateFlow<DataNotificator<List<MenuItem>>>
        get() = mMenuItemsWorker.stateMenuItems

    override val sharedLogOut: SharedFlow<Unit>
        get() = mMenuItemsWorker.sharedLogOut

    /*
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
     * Relations states
     * */
    override val stateUserRelations: StateFlow<DataNotificator<List<AdaptiveRelation>>>
        get() = mRelationsWorker.stateUserRelations

    override val sharedUserRelationsUpdates: SharedFlow<DataNotificator<AdaptiveRelation>>
        get() = mRelationsWorker.sharedUserRelationsUpdates

    /**
     * Register states
     * */
    override val stateUserRegister: StateFlow<Boolean?>
        get() = mRegisterWorker.stateUserRegister

    override val userLogin: String
        get() = mRegisterWorker.userLogin ?: ""

    /**
     * Other states
     * */
    override val sharedMessagesHolderUpdates: SharedFlow<AdaptiveMessage>
        get() = mMessagesWorker.sharedMessagesHolderUpdates

    override val stockHistoryConverter: StockHistoryConverter
        get() = StockHistoryConverter

    override suspend fun prepareData() {
        prepareCompaniesData()
        prepareSearchesHistory()
        mRelationsWorker.prepareUserRelations()
        mRegisterWorker.prepareUserRegisterState()
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
            onLogStateChanged = { logState ->
                mRegisterWorker.onLogIn()
                mRelationsWorker.onLogIn()
                mMenuItemsWorker.onLogStateChanged(logState)
            },
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
        mRegisterWorker.onLogOut()
        mRelationsWorker.onLogOut()
        mMessagesWorker.onLogOut()
        mAuthenticationWorker.logOut()
        mMenuItemsWorker.onLogStateChanged(mRepository.isUserAuthenticated())
    }

    override suspend fun addCompanyToFavourites(company: AdaptiveCompany, ignoreError: Boolean) {
        mCompaniesMediator.addCompanyToFavourites(company, ignoreError,
            onAdd = { addedCompany ->
                mSynchronizationManager.onCompanyAddedToLocal(addedCompany)
            })
    }

    override suspend fun removeCompanyFromFavourites(company: AdaptiveCompany) {
        mCompaniesMediator.removeCompanyFromFavourites(company,
            onRemove = { removedCompany ->
                mSynchronizationManager.onCompanyRemovedFromLocal(removedCompany)
            })
    }

    override suspend fun tryToRegister(login: String): Flow<Boolean> {
        return mRegisterWorker.tryToRegister(
            login,
            onError = { message -> mErrorsWorker.onRegisterError(message) }
        )
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

    override fun prepareForWebSocketReconnection() {
        mWebSocketWorker.prepareToWebSocketReconnection()
    }

    override suspend fun getMessagesStateForLogin(
        associatedUserLogin: String
    ): StateFlow<DataNotificator<AdaptiveMessagesHolder>> {
        return mMessagesWorker.getMessagesStateForLogin(associatedUserLogin)
    }

    override suspend fun loadMessagesAssociatedWithLogin(associatedLogin: String) {
        mMessagesWorker.loadMessagesAssociatedWithLogin(
            sourceUserLogin = userLogin,
            associatedLogin = associatedLogin,
            onError = { message -> mErrorsWorker.onLoadMessageError(message) }
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

    override suspend fun setFirstTimeLaunchState(state: Boolean) {
        mRepository.setFirstTimeLaunchState(state)
    }

    override suspend fun getFirstTimeLaunchState(): Boolean {
        val repositoryResponse = mRepository.getFirstTimeLaunchState()
        return if (repositoryResponse is RepositoryResponse.Success) {
            repositoryResponse.data
        } else false
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
}