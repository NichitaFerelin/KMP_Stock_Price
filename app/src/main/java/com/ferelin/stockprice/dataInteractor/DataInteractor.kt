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
import com.ferelin.repository.adaptiveModels.AdaptiveSearchRequest
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.common.menu.MenuItem
import com.ferelin.stockprice.dataInteractor.dataManager.DataMediator
import com.ferelin.stockprice.dataInteractor.dataManager.workers.ErrorsWorker
import com.ferelin.stockprice.dataInteractor.local.LocalInteractor
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorResponse
import com.ferelin.stockprice.dataInteractor.syncManager.SynchronizationManager
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.StockHistoryConverter
import com.ferelin.stockprice.utils.findCompany
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [DataInteractor] is MAIN and SINGLE entity for the UI layer interaction with data.
 *   - Providing states of data and errors.
 *   - Sending network requests to Repository using [mRepositoryHelper].
 *   - Sending local requests to Repository using [mLocalInteractor].
 *   - Sending errors to [mErrorsWorker].
 *   - Providing states about data loading to [mDataMediator].
 */
@Singleton
class DataInteractor @Inject constructor(
    private val mRepositoryHelper: Repository,
    private val mLocalInteractor: LocalInteractor,
    private val mDataMediator: DataMediator,
    private val mErrorsWorker: ErrorsWorker,
    private val mSynchronizationManager: SynchronizationManager
) : DataInteractorHelper {

    val stateCompanies: StateFlow<DataNotificator<ArrayList<AdaptiveCompany>>>
        get() = mDataMediator.companiesWorker.stateCompanies

    val sharedCompaniesUpdates: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mDataMediator.companiesWorker.sharedCompaniesUpdates

    val stateFavouriteCompanies: StateFlow<DataNotificator<ArrayList<AdaptiveCompany>>>
        get() = mDataMediator.favouriteCompaniesWorker.stateFavouriteCompanies

    val stateCompanyForObserver: StateFlow<AdaptiveCompany?>
        get() = mDataMediator.favouriteCompaniesWorker.stateCompanyForObserver

    val sharedFavouriteCompaniesUpdates: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mDataMediator.favouriteCompaniesWorker.sharedFavouriteCompaniesUpdates

    val stateSearchRequests: StateFlow<DataNotificator<ArrayList<AdaptiveSearchRequest>>>
        get() = mDataMediator.searchRequestsWorker.stateSearchRequests

    val statePopularSearchRequests: StateFlow<DataNotificator<ArrayList<AdaptiveSearchRequest>>>
        get() = mDataMediator.searchRequestsWorker.statePopularSearchRequests

    val stateIsNetworkAvailable: StateFlow<Boolean>
        get() = mDataMediator.networkConnectivityWorker.stateIsNetworkAvailable

    val stateFirstTimeLaunch: StateFlow<Boolean?>
        get() = mDataMediator.firstTimeLaunchWorker.stateFirstTimeLaunch

    val stateMenuItems: StateFlow<DataNotificator<List<MenuItem>>>
        get() = mDataMediator.menuItemsWorker.stateMenuItems

    val sharedApiLimitError: SharedFlow<String>
        get() = mErrorsWorker.sharedApiLimitError

    val sharedPrepareCompaniesError: SharedFlow<String>
        get() = mErrorsWorker.sharedPrepareCompaniesError

    val sharedLoadCompanyQuoteError: SharedFlow<String>
        get() = mErrorsWorker.sharedLoadCompanyQuoteError

    val sharedLoadStockCandlesError: SharedFlow<String>
        get() = mErrorsWorker.sharedLoadStockCandlesError

    val sharedLoadCompanyNewsError: SharedFlow<String>
        get() = mErrorsWorker.sharedLoadCompanyNewsError

    val sharedLoadSearchRequestsError: SharedFlow<String>
        get() = mErrorsWorker.sharedLoadSearchRequestsError

    val sharedFavouriteCompaniesLimitReached: SharedFlow<String>
        get() = mErrorsWorker.sharedFavouriteCompaniesLimitReached

    val sharedAuthenticationError: SharedFlow<String>
        get() = mErrorsWorker.sharedAuthenticationError

    val sharedLogOut: SharedFlow<Unit>
        get() = mDataMediator.menuItemsWorker.sharedLogOut

    val stockHistoryConverter: StockHistoryConverter
        get() = StockHistoryConverter

    override suspend fun prepareData() {
        prepareCompaniesData()
        prepareSearchesHistory()
        prepareFirstTimeLaunchState()
    }

    override suspend fun loadStockCandles(symbol: String): Flow<AdaptiveCompany> {
        return mRepositoryHelper.loadStockCandles(symbol)
            .onEach {
                when (it) {
                    is RepositoryResponse.Success -> mDataMediator.onStockCandlesLoaded(it)
                    is RepositoryResponse.Failed -> {
                        if (stateIsNetworkAvailable.value) {
                            mErrorsWorker.onLoadStockCandlesError(
                                it.message,
                                symbol
                            )
                        }
                    }
                }
            }
            .filter { it is RepositoryResponse.Success }
            .map { getCompany(symbol)!! }
    }

    override suspend fun loadCompanyNews(symbol: String): Flow<AdaptiveCompany> {
        return mRepositoryHelper.loadCompanyNews(symbol)
            .onEach {
                when (it) {
                    is RepositoryResponse.Success -> mDataMediator.onCompanyNewsLoaded(it)
                    is RepositoryResponse.Failed -> {
                        if (stateIsNetworkAvailable.value) {
                            mErrorsWorker.onLoadCompanyNewsError(
                                it.message,
                                symbol
                            )
                        }
                    }
                }
            }
            .filter { it is RepositoryResponse.Success }
            .map { getCompany(symbol)!! }
    }

    override suspend fun loadCompanyQuote(
        symbol: String,
        position: Int,
        isImportant: Boolean
    ): Flow<AdaptiveCompany> {
        return mRepositoryHelper.loadCompanyQuote(symbol, position, isImportant)
            .onEach {
                when (it) {
                    is RepositoryResponse.Success -> mDataMediator.onCompanyQuoteLoaded(it)
                    is RepositoryResponse.Failed -> {
                        if (stateIsNetworkAvailable.value) {
                            mErrorsWorker.onLoadCompanyQuoteError(
                                it.message,
                                it.owner ?: ""
                            )
                        }
                    }
                }
            }
            .filter { it is RepositoryResponse.Success && it.owner != null }
            .map { getCompany((it as RepositoryResponse.Success).owner!!)!! }
    }

    override suspend fun openConnection(): Flow<AdaptiveCompany> {
        return mRepositoryHelper.openWebSocketConnection()
            .filter { it is RepositoryResponse.Success && it.owner != null }
            .onEach { mDataMediator.onWebSocketResponse(it as RepositoryResponse.Success) }
            .map { getCompany((it as RepositoryResponse.Success).owner!!)!! }
    }

    override suspend fun signIn(holderActivity: Activity, phone: String): Flow<RepositoryMessages> {
        return mRepositoryHelper.tryToSignIn(holderActivity, phone)
            .onEach { response ->
                when (response) {
                    is RepositoryResponse.Success -> {
                        if (response.data is RepositoryMessages.Ok) {
                            mDataMediator.onLogStateChanged(mRepositoryHelper.provideIsUserLogged())
                            mSynchronizationManager.onLogIn()
                        }
                    }
                    is RepositoryResponse.Failed -> mErrorsWorker.onAuthenticationError(response.message)
                }
            }
            .filter { it is RepositoryResponse.Success }
            .map { (it as RepositoryResponse.Success).data }
    }

    override fun logInWithCode(code: String) {
        mRepositoryHelper.logInWithCode(code)
    }

    override suspend fun logOut() {
        mRepositoryHelper.logOut()
        mSynchronizationManager.onLogOut()
        mDataMediator.onLogStateChanged(mRepositoryHelper.provideIsUserLogged())
    }

    override suspend fun cacheNewSearchRequest(searchText: String) {
        val changesActionsHistory = mDataMediator.cacheNewSearchRequest(searchText)
        mSynchronizationManager.onSearchRequestsChanged(changesActionsHistory)
    }

    override suspend fun addCompanyToFavourite(adaptiveCompany: AdaptiveCompany) {
        mDataMediator.onAddFavouriteCompany(adaptiveCompany).also { isAdded ->
            /**
             * The company may not be accepted to favourites.
             * If accepted -> then notify to sync manager
             */
            if (isAdded) {
                mSynchronizationManager.onCompanyAddedToLocal(adaptiveCompany)
            }
        }
    }

    override suspend fun removeCompanyFromFavourite(adaptiveCompany: AdaptiveCompany) {
        mDataMediator.onRemoveFavouriteCompany(adaptiveCompany)
        mSynchronizationManager.onCompanyRemovedFromLocal(adaptiveCompany)
    }

    override suspend fun addCompanyToFavourite(symbol: String) {
        getCompany(symbol)?.let { addCompanyToFavourite(it) }
    }

    override suspend fun removeCompanyFromFavourite(symbol: String) {
        getCompany(symbol)?.let { removeCompanyFromFavourite(it) }
    }

    override suspend fun setFirstTimeLaunchState(state: Boolean) {
        mLocalInteractor.setFirstTimeLaunchState(state)
    }

    override fun prepareToWebSocketReconnection() {
        mRepositoryHelper.invalidateWebSocketConnection()
        mDataMediator.subscribeItemsOnLiveTimeUpdates()
    }

    override fun provideNetworkStateFlow(): Flow<Boolean> {
        return stateIsNetworkAvailable.onEach {
            mSynchronizationManager.onNetworkStateChanged(it)
        }
    }

    private suspend fun prepareCompaniesData() {
        val responseCompanies = mLocalInteractor.getCompanies()
        if (responseCompanies is LocalInteractorResponse.Success) {
            mDataMediator.onCompaniesDataPrepared(responseCompanies.companies)
        } else mErrorsWorker.onPrepareCompaniesError()
    }

    private suspend fun prepareSearchesHistory() {
        val responseSearchesHistory = mLocalInteractor.getSearchRequestsHistory()
        if (responseSearchesHistory is LocalInteractorResponse.Success) {
            mDataMediator.onSearchRequestsHistoryPrepared(responseSearchesHistory.searchesHistory)
        } else mErrorsWorker.onLoadSearchRequestsError()
    }

    private suspend fun prepareFirstTimeLaunchState() {
        val firstTimeLaunchResponse = mLocalInteractor.getFirstTimeLaunchState()
        mDataMediator.onFirstTimeLaunchStateResponse(firstTimeLaunchResponse)
    }

    private fun getCompany(symbol: String): AdaptiveCompany? {
        return findCompany(mDataMediator.companiesWorker.companies, symbol)
    }
}