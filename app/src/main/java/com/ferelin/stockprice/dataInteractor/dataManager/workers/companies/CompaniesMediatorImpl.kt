package com.ferelin.stockprice.dataInteractor.dataManager.workers.companies

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

import com.ferelin.repository.Repository
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.dataInteractor.dataManager.workers.companies.defaults.CompaniesWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.companies.defaults.CompaniesWorkerStates
import com.ferelin.stockprice.dataInteractor.dataManager.workers.companies.favourites.FavouriteCompaniesWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.network.NetworkConnectivityWorkerStates
import com.ferelin.stockprice.dataInteractor.syncManager.SynchronizationManager
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.findCompany
import kotlinx.coroutines.flow.*
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [CompaniesMediatorImpl] is an implementation of the pattern Mediator, that helps to work with data.
 */

@Singleton
open class CompaniesMediatorImpl @Inject constructor(
    private val mRepository: Repository,
    private val mCompaniesWorker: CompaniesWorker,
    private val mCompaniesWorkerStates: CompaniesWorkerStates,
    private val mFavouriteCompaniesWorker: FavouriteCompaniesWorker,
    private val mSynchronizationManager: SynchronizationManager,
    private val mNetworkConnectivityWorkerStates: NetworkConnectivityWorkerStates,
) : CompaniesMediator {

    private val mStateIsNetworkAvailable: StateFlow<Boolean>
        get() = mNetworkConnectivityWorkerStates.stateIsNetworkAvailable

    override fun onCompaniesDataPrepared(companies: List<AdaptiveCompany>) {
        mCompaniesWorker.onCompaniesDataPrepared(companies)
        mFavouriteCompaniesWorker.onFavouriteCompaniesDataPrepared(companies)
    }

    override suspend fun addCompanyToFavourites(company: AdaptiveCompany, ignoreError: Boolean) {
        mFavouriteCompaniesWorker.addCompanyToFavourites(company, ignoreError)
            ?.let { addedCompany ->
                mCompaniesWorker.onCompanyChanged(DataNotificator.ItemUpdatedCommon(addedCompany))
                mSynchronizationManager.onCompanyAddedToLocal(addedCompany)
            }
    }

    override suspend fun removeCompanyFromFavourites(company: AdaptiveCompany) {
        val updatedCompany = mFavouriteCompaniesWorker.removeCompanyFromFavourites(company)
        mSynchronizationManager.onCompanyRemovedFromLocal(company)
        mCompaniesWorker.onCompanyChanged(DataNotificator.ItemUpdatedCommon(updatedCompany))
    }

    override suspend fun loadStockCandlesFromNetwork(
        symbol: String,
        onError: suspend (RepositoryMessages, String) -> Unit
    ): Flow<AdaptiveCompany> {
        return mRepository.loadStockCandles(symbol)
            .onEach { repositoryResponse ->
                when (repositoryResponse) {
                    is RepositoryResponse.Success -> {
                        onStockCandlesLoaded(repositoryResponse)
                    }
                    is RepositoryResponse.Failed -> {
                        if (mStateIsNetworkAvailable.value) {
                            onError.invoke(
                                repositoryResponse.message,
                                symbol
                            )
                        }
                    }
                }
            }
            .filter { it is RepositoryResponse.Success }
            .map { getCompany(symbol)!! }
    }

    override suspend fun loadCompanyNewsFromNetwork(
        symbol: String,
        onError: suspend (RepositoryMessages, String) -> Unit
    ): Flow<AdaptiveCompany> {
        return mRepository.loadCompanyNews(symbol)
            .onEach {
                when (it) {
                    is RepositoryResponse.Success -> onCompanyNewsLoaded(it)
                    is RepositoryResponse.Failed -> {
                        if (mStateIsNetworkAvailable.value) {
                            onError.invoke(
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

    override suspend fun loadCompanyQuoteFromNetwork(
        symbol: String,
        position: Int,
        isImportant: Boolean
    ): Flow<AdaptiveCompany> {
        return mRepository.loadCompanyQuote(symbol, position, isImportant)
            .filter { it is RepositoryResponse.Success && it.owner != null }
            .onEach { onCompanyQuoteLoaded(it as RepositoryResponse.Success) }
            .map { getCompany((it as RepositoryResponse.Success).owner!!)!! }
    }

    override suspend fun onLiveTimePriceChanged(
        response: RepositoryResponse.Success<AdaptiveWebSocketPrice>
    ) {
        mCompaniesWorker.onLiveTimePriceChanged(response)?.let { updatedCompany ->
            onDataChanged(updatedCompany, DataNotificator.ItemUpdatedLiveTime(updatedCompany))
        }
    }

    override fun subscribeItemsOnLiveTimeUpdates() {
        mFavouriteCompaniesWorker.subscribeCompaniesOnLiveTimeUpdates()
    }

    override fun getCompany(symbol: String): AdaptiveCompany? {
        return findCompany(mCompaniesWorkerStates.companies, symbol)
    }

    private suspend fun onCompanyQuoteLoaded(
        response: RepositoryResponse.Success<AdaptiveCompanyDayData>
    ) {
        mCompaniesWorker.onCompanyQuoteLoaded(response)?.let { updatedCompany ->
            onDataChanged(updatedCompany, DataNotificator.ItemUpdatedQuote(updatedCompany))
        }
    }

    private suspend fun onStockCandlesLoaded(
        response: RepositoryResponse.Success<AdaptiveCompanyHistory>
    ) {
        mCompaniesWorker.onStockCandlesLoaded(response)?.let { updatedCompany ->
            onDataChanged(updatedCompany)
        }
    }

    private suspend fun onCompanyNewsLoaded(
        response: RepositoryResponse.Success<AdaptiveCompanyNews>
    ) {
        mCompaniesWorker.onCompanyNewsLoaded(response)?.let { updatedCompany ->
            onDataChanged(updatedCompany)
        }
    }

    private suspend fun onDataChanged(
        company: AdaptiveCompany,
        notification: DataNotificator<AdaptiveCompany> = DataNotificator.ItemUpdatedCommon(company)
    ) {
        mCompaniesWorker.onCompanyChanged(notification)
        mFavouriteCompaniesWorker.onFavouriteCompanyChanged(company)
    }
}