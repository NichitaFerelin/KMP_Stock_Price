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
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveMessage
import com.ferelin.repository.adaptiveModels.AdaptiveMessagesHolder
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.common.menu.MenuItem
import com.ferelin.stockprice.dataInteractor.dataManager.dataMediator.DataMediator
import com.ferelin.stockprice.dataInteractor.helpers.apiHelper.ApiHelper
import com.ferelin.stockprice.dataInteractor.helpers.authenticationHelper.AuthenticationHelper
import com.ferelin.stockprice.dataInteractor.helpers.favouriteCompaniesHelper.FavouriteCompaniesHelper
import com.ferelin.stockprice.dataInteractor.helpers.messagesHelper.MessagesHelper
import com.ferelin.stockprice.dataInteractor.helpers.registerHelper.RegisterHelper
import com.ferelin.stockprice.dataInteractor.helpers.webHelper.WebHelper
import com.ferelin.stockprice.dataInteractor.syncManager.SynchronizationManager
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.StockHistoryConverter
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [DataInteractorImpl] is MAIN and SINGLE entity for the UI layer interaction with data.
 *   - Providing states of data and errors.
 *   - Sending network requests to Repository using [mRepositoryHelper].
 *   - Sending local requests to Repository using [mLocalInteractor].
 *   - Sending errors to [mErrorsWorker].
 *   - Providing states about data loading to [mDataMediator].
 */
@Singleton
class DataInteractorImpl @Inject constructor(
    private val mRepositoryHelper: Repository,
    private val mDataMediator: DataMediator,
    private val mSynchronizationManager: SynchronizationManager,
    // Helpers
    private val mApiHelper: ApiHelper,
    private val mAuthenticationHelper: AuthenticationHelper,
    private val mFavouriteCompaniesHelper: FavouriteCompaniesHelper,
    private val mMessagesHelper: MessagesHelper,
    private val mRegisterHelper: RegisterHelper,
    private val mWebHelper: WebHelper
) : DataInteractor {

    override val stateCompanies: StateFlow<DataNotificator<ArrayList<AdaptiveCompany>>>
        get() = mDataMediator.companiesWorker.stateCompanies

    override val sharedCompaniesUpdates: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mDataMediator.companiesWorker.sharedCompaniesUpdates

    override val stateFavouriteCompanies: StateFlow<DataNotificator<ArrayList<AdaptiveCompany>>>
        get() = mDataMediator.favouriteCompaniesWorker.stateFavouriteCompanies

    override val stateCompanyForObserver: StateFlow<AdaptiveCompany?>
        get() = mDataMediator.favouriteCompaniesWorker.stateCompanyForObserver

    override val sharedFavouriteCompaniesUpdates: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mDataMediator.favouriteCompaniesWorker.sharedFavouriteCompaniesUpdates

    override val stateSearchRequests: StateFlow<DataNotificator<ArrayList<AdaptiveSearchRequest>>>
        get() = mDataMediator.searchRequestsWorker.stateSearchRequests

    override val statePopularSearchRequests: StateFlow<DataNotificator<ArrayList<AdaptiveSearchRequest>>>
        get() = mDataMediator.searchRequestsWorker.statePopularSearchRequests

    override val stateIsNetworkAvailable: StateFlow<Boolean>
        get() = mDataMediator.networkConnectivityWorker.stateIsNetworkAvailable

    override val stateMenuItems: StateFlow<DataNotificator<List<MenuItem>>>
        get() = mDataMediator.menuItemsWorker.stateMenuItems

    override val sharedMessagesHolderUpdates: SharedFlow<AdaptiveMessage>
        get() = mDataMediator.messagesWorker.sharedMessagesHolderUpdates

    override val sharedLoadMessagesError: SharedFlow<String>
        get() = mDataMediator.errorsWorker.sharedLoadMessagesError

    override val sharedApiLimitError: SharedFlow<String>
        get() = mDataMediator.errorsWorker.sharedApiLimitError

    override val sharedPrepareCompaniesError: SharedFlow<String>
        get() = mDataMediator.errorsWorker.sharedPrepareCompaniesError

    override val sharedLoadStockCandlesError: SharedFlow<String>
        get() = mDataMediator.errorsWorker.sharedLoadStockCandlesError

    override val sharedLoadCompanyNewsError: SharedFlow<String>
        get() = mDataMediator.errorsWorker.sharedLoadCompanyNewsError

    override val sharedLoadSearchRequestsError: SharedFlow<String>
        get() = mDataMediator.errorsWorker.sharedLoadSearchRequestsError

    override val sharedFavouriteCompaniesLimitReached: SharedFlow<String>
        get() = mDataMediator.errorsWorker.sharedFavouriteCompaniesLimitReached

    override val sharedAuthenticationError: SharedFlow<String>
        get() = mDataMediator.errorsWorker.sharedAuthenticationError

    override val sharedRegisterError: SharedFlow<String>
        get() = mDataMediator.errorsWorker.sharedRegisterError

    override val sharedLogOut: SharedFlow<Unit>
        get() = mDataMediator.menuItemsWorker.sharedLogOut

    override val stockHistoryConverter: StockHistoryConverter
        get() = StockHistoryConverter

    override val companies: List<AdaptiveCompany>
        get() = mDataMediator.companiesWorker.companies

    override val searchRequests: List<AdaptiveSearchRequest>
        get() = mDataMediator.searchRequestsWorker.searchRequests

    override val userLogin: String?
        get() = mDataMediator.loginWorker.userLogin

    override suspend fun prepareData() {
        prepareCompaniesData()
        prepareSearchesHistory()
    }

    override suspend fun loadStockCandles(symbol: String): Flow<AdaptiveCompany> {
        return mApiHelper.loadStockCandles(symbol)
    }

    override suspend fun loadCompanyNews(symbol: String): Flow<AdaptiveCompany> {
        return mApiHelper.loadCompanyNews(symbol)
    }

    override suspend fun loadCompanyQuote(
        symbol: String,
        position: Int,
        isImportant: Boolean
    ): Flow<AdaptiveCompany> {
        return mApiHelper.loadCompanyQuote(symbol, position, isImportant)
    }

    override suspend fun signIn(holderActivity: Activity, phone: String): Flow<RepositoryMessages> {
        return mAuthenticationHelper.signIn(holderActivity, phone)
    }

    override fun isUserLogged(): Boolean {
        return mAuthenticationHelper.isUserLogged()
    }

    override fun logInWithCode(code: String) {
        mAuthenticationHelper.logInWithCode(code)
    }

    override suspend fun logOut() {
        mAuthenticationHelper.logOut()
    }

    override suspend fun addCompanyToFavourite(adaptiveCompany: AdaptiveCompany) {
        mFavouriteCompaniesHelper.addCompanyToFavourite(adaptiveCompany)
    }

    override suspend fun removeCompanyFromFavourite(adaptiveCompany: AdaptiveCompany) {
        mFavouriteCompaniesHelper.removeCompanyFromFavourite(adaptiveCompany)
    }

    override suspend fun addCompanyToFavourite(symbol: String) {
        mFavouriteCompaniesHelper.addCompanyToFavourite(symbol)
    }

    override suspend fun removeCompanyFromFavourite(symbol: String) {
        mFavouriteCompaniesHelper.removeCompanyFromFavourite(symbol)
    }

    override suspend fun tryToRegister(login: String): Flow<Boolean> {
        return mRegisterHelper.tryToRegister(login)
    }

    override suspend fun isUserRegistered(): Boolean {
        return mRegisterHelper.isUserRegistered()
    }

    override suspend fun findUser(login: String): Boolean {
        return mRegisterHelper.findUser(login)
    }

    override suspend fun openConnection(): Flow<AdaptiveCompany> {
        return mWebHelper.openConnection()
    }

    override fun prepareToWebSocketReconnection() {
        mWebHelper.prepareToWebSocketReconnection()
    }

    override suspend fun cacheNewSearchRequest(searchText: String) {
        val changesActionsHistory = mDataMediator.cacheNewSearchRequest(searchText)
        mSynchronizationManager.onSearchRequestsChanged(changesActionsHistory)
    }

    override suspend fun getMessagesForLogin(
        login: String
    ): StateFlow<DataNotificator<AdaptiveMessagesHolder>> {
        return mMessagesHelper.getMessagesForLogin(login)
    }

    override suspend fun loadMessagesAssociatedWithLogin(
        associatedLogin: String
    ): AdaptiveMessagesHolder? {
        return mMessagesHelper.loadMessagesAssociatedWithLogin(associatedLogin)
    }

    override suspend fun setFirstTimeLaunchState(state: Boolean) {
        mRepositoryHelper.setFirstTimeLaunchState(state)
    }

    override suspend fun getFirstTimeLaunchState(): Boolean {
        val repositoryResponse = mRepositoryHelper.getFirstTimeLaunchState()
        return if (repositoryResponse is RepositoryResponse.Success) {
            repositoryResponse.data
        } else false
    }

    override fun provideNetworkStateFlow(): Flow<Boolean> {
        return stateIsNetworkAvailable.onEach {
            mSynchronizationManager.onNetworkStateChanged(it)
        }
    }

    private suspend fun prepareCompaniesData() {
        val responseCompanies = mRepositoryHelper.getAllCompaniesFromLocalDb()
        if (responseCompanies is RepositoryResponse.Success) {
            mDataMediator.onCommonCompaniesDataPrepared(responseCompanies.data)
        } else mDataMediator.onPrepareCompaniesError()
    }

    private suspend fun prepareSearchesHistory() {
        val responseSearchesHistory = mRepositoryHelper.getSearchesHistoryFromLocalDb()
        if (responseSearchesHistory is RepositoryResponse.Success) {
            mDataMediator.onSearchRequestsHistoryPrepared(responseSearchesHistory.data)
        } else mDataMediator.onLoadSearchRequestsError()
    }
}