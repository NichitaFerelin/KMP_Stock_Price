package com.ferelin.stockprice.dataInteractor.dataManager

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
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.dataInteractor.dataManager.workers.*
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorResponse
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.actionHolder.ActionHolder
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [DataMediator] is an implementation of the pattern Mediator, that helps to work with data.
 */

@Singleton
open class DataMediator @Inject constructor(
    val companiesWorker: CompaniesWorker,
    val favouriteCompaniesWorker: FavouriteCompaniesWorker,
    val searchRequestsWorker: SearchRequestsWorker,
    val firstTimeLaunchWorker: FirstTimeLaunchWorker,
    val menuItemsWorker: MenuItemsWorker,
    val networkConnectivityWorker: NetworkConnectivityWorker
) {
    fun onCompaniesDataPrepared(companies: List<AdaptiveCompany>) {
        companiesWorker.onDataPrepared(companies)
        favouriteCompaniesWorker.onDataPrepared(companies)
    }

    suspend fun onStockCandlesLoaded(response: RepositoryResponse.Success<AdaptiveCompanyHistory>) {
        companiesWorker.onStockCandlesLoaded(response)?.let { updatedCompany ->
            onDataChanged(updatedCompany)
        }
    }

    suspend fun onCompanyNewsLoaded(response: RepositoryResponse.Success<AdaptiveCompanyNews>) {
        companiesWorker.onCompanyNewsLoaded(response)?.let { updatedCompany ->
            onDataChanged(updatedCompany)
        }
    }

    suspend fun onCompanyQuoteLoaded(response: RepositoryResponse.Success<AdaptiveCompanyDayData>) {
        companiesWorker.onCompanyQuoteLoaded(response)?.let { updatedCompany ->
            onDataChanged(updatedCompany, DataNotificator.ItemUpdatedQuote(updatedCompany))
        }
    }

    suspend fun onWebSocketResponse(response: RepositoryResponse.Success<AdaptiveWebSocketPrice>) {
        companiesWorker.onLiveTimePriceChanged(response)?.let { updatedCompany ->
            onDataChanged(updatedCompany, DataNotificator.ItemUpdatedLiveTime(updatedCompany))
        }
    }

    suspend fun onAddFavouriteCompany(
        company: AdaptiveCompany,
        ignoreError: Boolean = false
    ): Boolean {
        favouriteCompaniesWorker.addCompanyToFavourites(company, ignoreError)?.let { addedCompany ->
            companiesWorker.onCompanyChanged(DataNotificator.ItemUpdatedCommon(addedCompany))
            return true
        }
        return false
    }

    suspend fun onRemoveFavouriteCompany(company: AdaptiveCompany) {
        val updatedCompany = favouriteCompaniesWorker.removeCompanyFromFavourites(company)
        companiesWorker.onCompanyChanged(DataNotificator.ItemUpdatedCommon(updatedCompany))
    }

    fun onSearchRequestsHistoryPrepared(searches: List<AdaptiveSearchRequest>) {
        searchRequestsWorker.onDataPrepared(searches)
    }

    fun subscribeItemsOnLiveTimeUpdates() {
        favouriteCompaniesWorker.subscribeCompaniesOnLiveTimeUpdates()
    }

    fun onFirstTimeLaunchStateResponse(response: LocalInteractorResponse) {
        firstTimeLaunchWorker.onResponse(response)
    }

    suspend fun onLogStateChanged(isLogged: Boolean) {
        menuItemsWorker.onLogStateChanged(isLogged)
    }

    suspend fun cacheNewSearchRequest(searchText: String): List<ActionHolder<String>> {
        return searchRequestsWorker.cacheNewSearchRequest(searchText)
    }

    suspend fun clearSearchRequests() {
        searchRequestsWorker.clearSearchRequests()
    }

    private suspend fun onDataChanged(
        company: AdaptiveCompany,
        notification: DataNotificator<AdaptiveCompany> = DataNotificator.ItemUpdatedCommon(company)
    ) {
        companiesWorker.onCompanyChanged(notification)
        favouriteCompaniesWorker.onCompanyChanged(company)
    }
}