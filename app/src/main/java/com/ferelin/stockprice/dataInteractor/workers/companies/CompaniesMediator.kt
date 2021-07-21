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
import com.ferelin.repository.adaptiveModels.AdaptiveCompanyHistory
import com.ferelin.repository.adaptiveModels.AdaptiveCompanyNews
import com.ferelin.repository.adaptiveModels.AdaptiveWebSocketPrice
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.dataInteractor.workers.companies.defaults.CompaniesWorker
import com.ferelin.stockprice.dataInteractor.workers.companies.favourites.FavouriteCompaniesWorker
import com.ferelin.stockprice.dataInteractor.workers.errors.ErrorsWorker
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
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
    private val mErrorsWorker: ErrorsWorker
) : CompaniesMediatorStates {

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

    private var mResponseStockPriceJob: Job? = null

    init {
        prepareCompaniesData()

        mFavouriteCompaniesWorker.setOnCompanyAddedCallback {
            mAppScope.launch {
                mCompaniesWorker.onCompanyChanged(DataNotificator.ItemUpdatedCommon(it))
            }
        }

        mFavouriteCompaniesWorker.setOnCompanyRemovedCallback {
            mAppScope.launch {
                mCompaniesWorker.onCompanyChanged(DataNotificator.ItemUpdatedCommon(it))
            }
        }
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

    suspend fun loadStockHistory(symbol: String): AdaptiveCompanyHistory? {
        return when (val repositoryResponse = mRepository.loadStockHistory(symbol)) {
            is RepositoryResponse.Success -> mCompaniesWorker.onHistoryResponse(repositoryResponse)
            is RepositoryResponse.Failed -> {
                mErrorsWorker.onLoadStockHistoryError(repositoryResponse.message, symbol)
                null
            }
        }
    }

    suspend fun loadCompanyNews(symbol: String): AdaptiveCompanyNews? {
        return when (val repositoryResponse = mRepository.loadCompanyNews(symbol)) {
            is RepositoryResponse.Success -> mCompaniesWorker.onNewsResponse(repositoryResponse)
            is RepositoryResponse.Failed -> {
                mErrorsWorker.onLoadCompanyNewsError(repositoryResponse.message, symbol)
                null
            }
        }
    }

    fun sendRequestToLoadStockPrice(
        symbol: String,
        position: Int,
        isImportant: Boolean
    ) {
        collectResponseStockPrice()
        return mRepository.sendRequestToLoadPrice(symbol, position, isImportant)
    }

    private fun collectResponseStockPrice() {
        if (mResponseStockPriceJob == null) {
            mAppScope.launch {
                mResponseStockPriceJob = launch {
                    mRepository.getStockPriceResponseState().collect { response ->
                        if (response is RepositoryResponse.Success) {
                            mCompaniesWorker.onPriceResponse(response)
                        }
                    }
                }
            }
        }
    }

    suspend fun onLiveTimePriceResponse(
        repositoryResponse: RepositoryResponse.Success<AdaptiveWebSocketPrice>
    ) {
        mCompaniesWorker.onLiveTimePriceResponse(repositoryResponse)
    }

    fun subscribeItemsOnLiveTimeUpdates() {
        mFavouriteCompaniesWorker.subscribeCompaniesOnLiveTimeUpdates()
    }

    fun onLogIn() {
        mFavouriteCompaniesWorker.onLogIn()
    }

    fun onLogOut() {
        mFavouriteCompaniesWorker.onLogOut()
    }

    fun onNetworkLost() {
        mFavouriteCompaniesWorker.onNetworkLost()
    }

    suspend fun onNetworkAvailable() {
        mFavouriteCompaniesWorker.onNetworkAvailable()
    }

    private fun prepareCompaniesData() {
        mAppScope.launch {
            val localCompaniesResponse = mRepository.getAllCompaniesFromLocalDb()
            if (localCompaniesResponse is RepositoryResponse.Success) {
                onCompaniesDataPrepared(localCompaniesResponse.data)
            } else mErrorsWorker.onPrepareCompaniesError()
        }
    }

    private fun onCompaniesDataPrepared(companies: List<AdaptiveCompany>) {
        mCompaniesWorker.onCompaniesDataPrepared(companies)
        mFavouriteCompaniesWorker.onCompaniesDataPrepared(companies)
    }
}