package com.ferelin.stockprice.dataInteractor.dataManager

import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.dataInteractor.dataManager.workers.CompaniesStateWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.FavouriteCompaniesStateWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.FirstTimeLaunchStateWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.SearchRequestsStateWorker
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorResponse
import com.ferelin.stockprice.utils.DataNotificator

/*
* Class DataMediator is an implementation of the pattern Mediator.
* Is designed to send the requests to the appropriate objects.
* */
class DataMediator(
    val companiesWorker: CompaniesStateWorker,
    val favouriteCompaniesWorker: FavouriteCompaniesStateWorker,
    val searchRequestsWorker: SearchRequestsStateWorker,
    val firstTimeLaunchStateWorker: FirstTimeLaunchStateWorker
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
        companiesWorker.onWebSocketResponse(response)?.let { updatedCompany ->
            onDataChanged(updatedCompany, DataNotificator.ItemUpdatedLiveTime(updatedCompany))
        }
    }

    suspend fun onAddFavouriteCompany(company: AdaptiveCompany) {
        favouriteCompaniesWorker.addCompanyToFavourites(company)?.let { addedCompany ->
            companiesWorker.onCompanyChanged(DataNotificator.ItemUpdatedDefault(addedCompany))
        }
    }

    suspend fun onRemoveFavouriteCompany(company: AdaptiveCompany) {
        val updateCompany = favouriteCompaniesWorker.removeCompanyFromFavourites(company)
        companiesWorker.onCompanyChanged(DataNotificator.ItemUpdatedDefault(updateCompany))
    }

    fun onSearchRequestsHistoryPrepared(searches: List<AdaptiveSearchRequest>) {
        searchRequestsWorker.onDataPrepared(searches)
    }

    fun resubscribeItemsOnLiveTimeUpdates() {
        favouriteCompaniesWorker.resubscribeItemsOnLiveTimeUpdates()
    }

    fun onFirstTimeLaunchStateResponse(response: LocalInteractorResponse) {
        firstTimeLaunchStateWorker.onResponse(response)
    }

    suspend fun onNewSearch(searchText: String) {
        searchRequestsWorker.onNewSearch(searchText)
    }

    private suspend fun onDataChanged(
        company: AdaptiveCompany,
        notification: DataNotificator<AdaptiveCompany> = DataNotificator.ItemUpdatedDefault(company)
    ) {
        companiesWorker.onCompanyChanged(notification)
        favouriteCompaniesWorker.onCompanyChanged(company)
    }
}