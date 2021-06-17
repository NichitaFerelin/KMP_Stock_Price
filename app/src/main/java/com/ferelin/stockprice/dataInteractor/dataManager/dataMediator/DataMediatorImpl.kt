package com.ferelin.stockprice.dataInteractor.dataManager.dataMediator

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

import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.dataInteractor.dataManager.workers.companies.CompaniesWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.companies.CompaniesWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.errors.ErrorsWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.errors.ErrorsWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.favouritesCompanies.FavouriteCompaniesWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.favouritesCompanies.FavouriteCompaniesWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.login.LoginWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.login.LoginWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.menuItems.MenuItemsWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.menuItems.MenuItemsWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.messages.MessagesWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.messages.MessagesWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.network.NetworkConnectivityWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.searchRequests.SearchRequestsWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.searchRequests.SearchRequestsWorkerStates
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.actionHolder.ActionHolder
import com.ferelin.stockprice.utils.findCompany
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [DataMediatorImpl] is an implementation of the pattern Mediator, that helps to work with data.
 */

@Singleton
open class DataMediatorImpl @Inject constructor(
    /**
     * Provided states for Data Interactor object
     * */
    override val companiesWorker: CompaniesWorkerStates,
    override val favouriteCompaniesWorker: FavouriteCompaniesWorkerStates,
    override val searchRequestsWorker: SearchRequestsWorkerStates,
    override val menuItemsWorker: MenuItemsWorkerStates,
    override val networkConnectivityWorker: NetworkConnectivityWorkerStates,
    override val messagesWorker: MessagesWorkerStates,
    override val loginWorker: LoginWorkerStates,
    override val errorsWorker: ErrorsWorkerStates,

    /**
     * Provided to resolve interface methods
     * */
    private val mCompaniesWorker: CompaniesWorker,
    private val mFavouriteCompaniesWorker: FavouriteCompaniesWorker,
    private val mSearchRequestsWorker: SearchRequestsWorker,
    private val mMenuItemsWorker: MenuItemsWorker,
    private val mMessagesWorker: MessagesWorker,
    private val mLoginWorker: LoginWorker,
    private val mErrorsWorker: ErrorsWorker
) : DataMediator {

    override fun onCommonCompaniesDataPrepared(companies: List<AdaptiveCompany>) {
        mCompaniesWorker.onCompaniesDataPrepared(companies)
        mFavouriteCompaniesWorker.onFavouriteCompaniesDataPrepared(companies)
    }

    override fun onSearchRequestsHistoryPrepared(searches: List<AdaptiveSearchRequest>) {
        mSearchRequestsWorker.onSearchRequestsDataPrepared(searches)
    }

    override suspend fun onStockCandlesLoaded(
        response: RepositoryResponse.Success<AdaptiveCompanyHistory>
    ) {
        mCompaniesWorker.onStockCandlesLoaded(response)?.let { updatedCompany ->
            onDataChanged(updatedCompany)
        }
    }

    override suspend fun onCompanyNewsLoaded(
        response: RepositoryResponse.Success<AdaptiveCompanyNews>
    ) {
        mCompaniesWorker.onCompanyNewsLoaded(response)?.let { updatedCompany ->
            onDataChanged(updatedCompany)
        }
    }

    override suspend fun onCompanyQuoteLoaded(
        response: RepositoryResponse.Success<AdaptiveCompanyDayData>
    ) {
        mCompaniesWorker.onCompanyQuoteLoaded(response)?.let { updatedCompany ->
            onDataChanged(updatedCompany, DataNotificator.ItemUpdatedQuote(updatedCompany))
        }
    }

    override suspend fun onLiveTimePriceChanged(
        response: RepositoryResponse.Success<AdaptiveWebSocketPrice>
    ) {
        mCompaniesWorker.onLiveTimePriceChanged(response)?.let { updatedCompany ->
            onDataChanged(updatedCompany, DataNotificator.ItemUpdatedLiveTime(updatedCompany))
        }
    }

    override suspend fun onAddFavouriteCompany(
        company: AdaptiveCompany,
        ignoreError: Boolean
    ): Boolean {
        mFavouriteCompaniesWorker.addCompanyToFavourites(company, ignoreError)
            ?.let { addedCompany ->
                mCompaniesWorker.onCompanyChanged(DataNotificator.ItemUpdatedCommon(addedCompany))
                return true
            }
        return false
    }

    override suspend fun onRemoveFavouriteCompany(company: AdaptiveCompany) {
        val updatedCompany = mFavouriteCompaniesWorker.removeCompanyFromFavourites(company)
        mCompaniesWorker.onCompanyChanged(DataNotificator.ItemUpdatedCommon(updatedCompany))
    }

    override fun subscribeItemsOnLiveTimeUpdates() {
        mFavouriteCompaniesWorker.subscribeCompaniesOnLiveTimeUpdates()
    }

    override fun getMessagesStateForLogin(
        associatedUserLogin: String
    ): StateFlow<DataNotificator<AdaptiveMessagesHolder>> {
        return mMessagesWorker.getMessagesStateForLogin(associatedUserLogin)
    }


    override suspend fun onLogStateChanged(isLogged: Boolean) {
        mMenuItemsWorker.onLogStateChanged(isLogged)
    }

    override suspend fun cacheNewSearchRequest(searchText: String): List<ActionHolder<String>> {
        return mSearchRequestsWorker.cacheNewSearchRequest(searchText)
    }

    override suspend fun clearSearchRequests() {
        mSearchRequestsWorker.clearSearchRequests()
    }

    override suspend fun onPrepareCompaniesError() {
        mErrorsWorker.onPrepareCompaniesError()
    }

    override suspend fun onLoadStockCandlesError(
        message: RepositoryMessages,
        companySymbol: String
    ) {
        mErrorsWorker.onLoadStockCandlesError(message, companySymbol)
    }

    override suspend fun onLoadCompanyNewsError(
        message: RepositoryMessages,
        companySymbol: String
    ) {
        mErrorsWorker.onLoadCompanyNewsError(message, companySymbol)
    }

    override suspend fun onLoadSearchRequestsError() {
        mErrorsWorker.onLoadSearchRequestsError()
    }

    override suspend fun onFavouriteCompaniesLimitReached() {
        mErrorsWorker.onFavouriteCompaniesLimitReached()
    }

    override suspend fun onAuthenticationError(message: RepositoryMessages) {
        mErrorsWorker.onAuthenticationError(message)
    }

    override suspend fun onRegisterError(message: RepositoryMessages) {
        mErrorsWorker.onRegisterError(message)
    }

    override suspend fun onLoadMessageError() {
        mErrorsWorker.onLoadMessageError()
    }

    override fun getCompany(symbol: String): AdaptiveCompany? {
        return findCompany(companiesWorker.companies, symbol)
    }

    private suspend fun onDataChanged(
        company: AdaptiveCompany,
        notification: DataNotificator<AdaptiveCompany> = DataNotificator.ItemUpdatedCommon(company)
    ) {
        mCompaniesWorker.onCompanyChanged(notification)
        mFavouriteCompaniesWorker.onFavouriteCompanyChanged(company)
    }
}