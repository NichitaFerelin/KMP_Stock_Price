package com.ferelin.stockprice.dataInteractor.dataManager

import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.dataInteractor.dataManager.workers.CompaniesStateWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.FavouriteCompaniesStateWorker
import com.ferelin.stockprice.dataInteractor.dataManager.workers.SearchRequestsStateWorker
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class DataManager(
    private val mCompaniesStateWorker: CompaniesStateWorker,
    private val mCompaniesFavouriteStateWorker: FavouriteCompaniesStateWorker,
    private val mSearchRequestsStateWorker: SearchRequestsStateWorker
) {
    val companiesState: StateFlow<DataNotificator<List<AdaptiveCompany>>>
        get() = mCompaniesStateWorker.companiesState

    val companiesUpdateShared: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mCompaniesStateWorker.companiesUpdatesShared

    val favouriteCompaniesState: StateFlow<DataNotificator<List<AdaptiveCompany>>>
        get() = mCompaniesFavouriteStateWorker.favouriteCompaniesState

    val favouriteCompaniesUpdateShared: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mCompaniesFavouriteStateWorker.favouriteCompaniesUpdateShared

    val searchRequestsState: StateFlow<DataNotificator<List<AdaptiveSearchRequest>>>
        get() = mSearchRequestsStateWorker.searchRequestsState

    val searchRequestsUpdateShared: SharedFlow<List<AdaptiveSearchRequest>>
        get() = mSearchRequestsStateWorker.searchRequestsUpdateShared

    fun onCompaniesDataPrepared(companies: List<AdaptiveCompany>) {
        mCompaniesStateWorker.onDataPrepared(companies)
        mCompaniesFavouriteStateWorker.onDataPrepared(companies)
    }

    fun onSearchesDataPrepared(searches: List<AdaptiveSearchRequest>) {
        mSearchRequestsStateWorker.onDataPrepared(searches)
    }

    fun getCompany(symbol: String): AdaptiveCompany? {
        return mCompaniesStateWorker.findCompany(symbol)
    }

    suspend fun onStockCandlesLoaded(response: RepositoryResponse.Success<AdaptiveCompanyHistory>) {
        mCompaniesStateWorker.onStockCandlesLoaded(response)?.let { updatedCompany ->
            onDataChanged(updatedCompany)
        }
    }

    suspend fun onCompanyNewsLoaded(response: RepositoryResponse.Success<AdaptiveCompanyNews>) {
        mCompaniesStateWorker.onCompanyNewsLoaded(response)?.let { updatedCompany ->
            onDataChanged(updatedCompany)
        }
    }

    suspend fun onCompanyQuoteLoaded(response: RepositoryResponse.Success<AdaptiveCompanyDayData>) {
        mCompaniesStateWorker.onCompanyQuoteLoaded(response)?.let { updatedCompany ->
            onDataChanged(updatedCompany, DataNotificator.ItemUpdatedQuote(updatedCompany))
        }
    }

    suspend fun onWebSocketResponse(response: RepositoryResponse.Success<AdaptiveWebSocketPrice>) {
        mCompaniesStateWorker.onWebSocketResponse(response)?.let { updatedCompany ->
            onDataChanged(updatedCompany, DataNotificator.ItemUpdatedLiveTime(updatedCompany))
        }
    }

    suspend fun onAddFavouriteCompany(company: AdaptiveCompany) {
        mCompaniesFavouriteStateWorker.onAddFavouriteCompany(company)?.let { addedCompany ->
            mCompaniesStateWorker.onCompanyChanged(DataNotificator.ItemUpdatedDefault(addedCompany))
        }
    }

    suspend fun onRemoveFavouriteCompany(company: AdaptiveCompany) {
        val updateCompany = mCompaniesFavouriteStateWorker.onRemoveFavouriteCompany(company)
        mCompaniesStateWorker.onCompanyChanged(DataNotificator.ItemUpdatedDefault(updateCompany))
    }

    suspend fun onNewSearch(searchText: String) {
        mSearchRequestsStateWorker.onNewSearch(searchText)
    }

    private suspend fun onDataChanged(
        company: AdaptiveCompany,
        notification: DataNotificator<AdaptiveCompany> = DataNotificator.ItemUpdatedDefault(company)
    ) {
        mCompaniesStateWorker.onCompanyChanged(notification)
        mCompaniesFavouriteStateWorker.onCompanyChanged(company)
    }
}