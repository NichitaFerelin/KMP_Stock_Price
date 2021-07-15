package com.ferelin.stockprice.dataInteractor.workers.companies

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
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveCompanyDayData
import com.ferelin.repository.adaptiveModels.AdaptiveWebSocketPrice
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.dataInteractor.workers.companies.defaults.CompaniesWorker
import com.ferelin.stockprice.dataInteractor.workers.companies.defaults.CompaniesWorkerStates
import com.ferelin.stockprice.dataInteractor.workers.companies.favourites.FavouriteCompaniesWorker
import com.ferelin.stockprice.dataInteractor.workers.companies.favourites.FavouriteCompaniesWorkerStates
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [CompaniesMediator] is an implementation of the pattern Mediator, that helps to work with data.
 */

@Singleton
open class CompaniesMediator @Inject constructor(
    private val mRepository: Repository,
    private val mAppScope: CoroutineScope,
    private val mCompaniesWorker: CompaniesWorker,
    private val mFavouriteCompaniesWorker: FavouriteCompaniesWorker,
) : CompaniesWorkerStates, FavouriteCompaniesWorkerStates {

    override val stateCompanies: StateFlow<DataNotificator<List<AdaptiveCompany>>>
        get() = mCompaniesWorker.stateCompanies

    override val sharedCompaniesUpdates: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mCompaniesWorker.sharedCompaniesUpdates

    override val stateFavouriteCompanies: StateFlow<DataNotificator<List<AdaptiveCompany>>>
        get() = mFavouriteCompaniesWorker.stateFavouriteCompanies

    override val sharedFavouriteCompaniesUpdates: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mFavouriteCompaniesWorker.sharedFavouriteCompaniesUpdates

    override val stateCompanyForObserver: StateFlow<AdaptiveCompany?>
        get() = mFavouriteCompaniesWorker.stateCompanyForObserver

    init {
        prepareCompaniesData()
    }

    suspend fun addCompanyToFavourites(company: AdaptiveCompany, ignoreError: Boolean) {
        mFavouriteCompaniesWorker.addCompanyToFavourites(company, ignoreError)
            ?.let { addedCompany ->
                mCompaniesWorker.onCompanyChanged(DataNotificator.ItemUpdatedCommon(addedCompany))
            }
    }

    suspend fun removeCompanyFromFavourites(company: AdaptiveCompany) {
        val updatedCompany = mFavouriteCompaniesWorker.removeCompanyFromFavourites(company)
        mCompaniesWorker.onCompanyChanged(DataNotificator.ItemUpdatedCommon(updatedCompany))
    }

    suspend fun loadStockHistory(symbol: String) {
        val repositoryResponse = mRepository.loadStockHistory(symbol)
        if (repositoryResponse is RepositoryResponse.Success) {
            mCompaniesWorker.isDataChanged(
                symbolCompanyOwner = repositoryResponse.owner ?: "",
                compareStrategy = { it.companyHistory == repositoryResponse.data }
            )?.let { changedCompany ->
                onDataChanged(changedCompany)
            }
        }
    }

    suspend fun loadCompanyNews(symbol: String) {
        val repositoryResponse = mRepository.loadCompanyNews(symbol)
        if (repositoryResponse is RepositoryResponse.Success) {
            mCompaniesWorker.isDataChanged(
                symbolCompanyOwner = repositoryResponse.owner ?: "",
                compareStrategy = { it.companyNews == repositoryResponse.data }
            )?.let { changedCompany ->
                onDataChanged(changedCompany)
            }
        }
    }

    suspend fun loadStockPrice(
        symbol: String,
        position: Int,
        isImportant: Boolean
    ) {
        val response = mRepository.loadStockPrice(symbol, position, isImportant)
        if (response is RepositoryResponse.Success) {
            onCompanyQuoteLoaded(response)
        }
    }

    fun onLiveTimePriceChanged(
        repositoryResponse: RepositoryResponse.Success<AdaptiveWebSocketPrice>
    ) {
        mCompaniesWorker.isDataChanged(
            symbolCompanyOwner = repositoryResponse.owner ?: "",
            compareStrategy = { it.companyDayData.currentPrice == repositoryResponse.data.price }
        )?.let { changedCompany ->
            mCompaniesWorker.onLiveTimePriceChanged(changedCompany, repositoryResponse.data)
        }
    }

    fun subscribeItemsOnLiveTimeUpdates() {
        mFavouriteCompaniesWorker.subscribeCompaniesOnLiveTimeUpdates()
    }

    fun onLogOut() {
        mFavouriteCompaniesWorker.onLogOut()
    }

    fun onNetworkLost() {
        mFavouriteCompaniesWorker.onNetworkLost()
    }

    fun onNetworkAvailable() {
        mFavouriteCompaniesWorker.onNetworkAvailable()
    }

    private suspend fun onCompanyQuoteLoaded(
        response: RepositoryResponse.Success<AdaptiveCompanyDayData>
    ) {
        mCompaniesWorker.isDataChanged(
            symbolCompanyOwner = response.owner ?: "",
            compareStrategy = { it.companyDayData.currentPrice == response.data.currentPrice }
        )?.let { changedCompany ->
            onDataChanged(changedCompany)
        }
    }

    private fun prepareCompaniesData() {
        mAppScope.launch {
            val localCompaniesResponse = mRepository.getAllCompaniesFromLocalDb()
            if (localCompaniesResponse is RepositoryResponse.Success) {
                onCompaniesDataPrepared(localCompaniesResponse.data)
            }
        }
    }

    private fun onCompaniesDataPrepared(companies: List<AdaptiveCompany>) {
        mCompaniesWorker.onCompaniesDataPrepared(companies)
        mFavouriteCompaniesWorker.onFavouriteCompaniesDataPrepared(companies)
    }

    private suspend fun onDataChanged(
        company: AdaptiveCompany,
        notification: DataNotificator<AdaptiveCompany> = DataNotificator.ItemUpdatedCommon(company)
    ) {
        mCompaniesWorker.onCompanyChanged(notification)

        // TODO is really need to notify ?
        mFavouriteCompaniesWorker.onFavouriteCompanyChanged(company)
    }
}