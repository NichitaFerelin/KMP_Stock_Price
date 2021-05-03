package com.ferelin.stockprice.dataInteractor.dataManager

import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.dataInteractor.dataManager.workers.CompaniesWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.FavouriteCompaniesWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.FirstTimeLaunchWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.SearchRequestsWorker
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorResponse
import com.ferelin.stockprice.utils.DataNotificator

/**
 * [DataMediator] is an implementation of the pattern Mediator, that helps to work with data.
 */
class DataMediator(
    val companiesWorker: CompaniesWorker,
    val favouriteCompaniesWorker: FavouriteCompaniesWorker,
    val searchRequestsWorker: SearchRequestsWorker,
    val firstTimeLaunchWorker: FirstTimeLaunchWorker
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

    suspend fun onAddFavouriteCompany(company: AdaptiveCompany) {
        favouriteCompaniesWorker.addCompanyToFavourites(company)?.let { addedCompany ->
            companiesWorker.onCompanyChanged(DataNotificator.ItemUpdatedCommon(addedCompany))
        }
    }

    suspend fun onRemoveFavouriteCompany(company: AdaptiveCompany) {
        val updateCompany = favouriteCompaniesWorker.removeCompanyFromFavourites(company)
        companiesWorker.onCompanyChanged(DataNotificator.ItemUpdatedCommon(updateCompany))
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

    suspend fun cacheNewSearchRequest(searchText: String) {
        searchRequestsWorker.cacheNewSearchRequest(searchText)
    }

    private suspend fun onDataChanged(
        company: AdaptiveCompany,
        notification: DataNotificator<AdaptiveCompany> = DataNotificator.ItemUpdatedCommon(company)
    ) {
        companiesWorker.onCompanyChanged(notification)
        favouriteCompaniesWorker.onCompanyChanged(company)
    }
}