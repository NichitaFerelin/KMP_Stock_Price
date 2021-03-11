package com.ferelin.stockprice.dataInteractor.dataManager

import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorHelper
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class DataManager(
    private val mLocalInteractorHelper: LocalInteractorHelper,
    private val mDataStylesManager: DataStylesManager
) {
    private var mCompanies: List<AdaptiveCompany>? = null
    private var mFavouriteCompanies: MutableList<AdaptiveCompany>? = null
    private var mSearchRequests: List<AdaptiveSearchRequest>? = null

    private val mCompaniesState = MutableStateFlow<DataNotificator<List<AdaptiveCompany>>>(
        DataNotificator.Loading()
    )
    val companiesState: StateFlow<DataNotificator<List<AdaptiveCompany>>>
        get() = mCompaniesState

    private val mFavouriteCompaniesState =
        MutableStateFlow<DataNotificator<MutableList<AdaptiveCompany>>>(
            DataNotificator.Loading()
        )
    val favouriteCompaniesState: StateFlow<DataNotificator<List<AdaptiveCompany>>>
        get() = mFavouriteCompaniesState

    fun onCompaniesDataPrepared(companies: List<AdaptiveCompany>) {
        mCompaniesState.value = DataNotificator.Loading()
        mFavouriteCompaniesState.value = DataNotificator.Loading()

        val favouriteCompanies = mutableListOf<AdaptiveCompany>()
        companies.forEachIndexed { index, company ->
            mDataStylesManager.applyStyles(company, index)
            if (company.isFavourite) favouriteCompanies.add(company)
        }
        mCompanies = companies
        mFavouriteCompanies = favouriteCompanies.toMutableList()
        mCompaniesState.value = DataNotificator.Success(companies)
        mFavouriteCompaniesState.value = DataNotificator.Success(favouriteCompanies.toMutableList())
    }

    private val mSearchRequestsState =
        MutableStateFlow<DataNotificator<MutableList<AdaptiveSearchRequest>>>(DataNotificator.Loading())
    val searchRequestsState: StateFlow<DataNotificator<MutableList<AdaptiveSearchRequest>>>
        get() = mSearchRequestsState

    private val mSearchRequestsUpdateState =
        MutableSharedFlow<DataNotificator<List<AdaptiveSearchRequest>>>()
    val searchRequestsUpdateState: SharedFlow<DataNotificator<List<AdaptiveSearchRequest>>>
        get() = mSearchRequestsUpdateState

    fun onSearchesDataPrepared(searches: List<AdaptiveSearchRequest>) {
        mSearchRequests = searches
        mSearchRequestsState.value = DataNotificator.Success(searches.toMutableList())
    }

    suspend fun onNewSearch(searchText: String) {
        val searchItem = AdaptiveSearchRequest(searchText)
        mSearchRequests?.let {
            val searchesData = it.toMutableList()
            for (index in 0 until searchesData.size - 1) {
                val item = searchesData[index]
                if (item.searchText.contains(searchText)) {
                    searchesData.remove(item)
                }
            }
            searchesData.add(searchItem)
            val dataToEmit = searchesData.toList()
            mSearchRequestsState.value = DataNotificator.Success(searchesData)
            mSearchRequestsUpdateState.emit(DataNotificator.Success(dataToEmit))
            mLocalInteractorHelper.setSearchesData(dataToEmit)
        }
    }

    private val mFavouriteCompaniesUpdateState =
        MutableSharedFlow<DataNotificator<AdaptiveCompany>>()
    val favouriteCompaniesUpdateState: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mFavouriteCompaniesUpdateState

    suspend fun onAddFavouriteCompany(company: AdaptiveCompany) {
        company.apply {
            isFavourite = true
            companyStyle.favouriteIconResource = mDataStylesManager.getIconDrawable(true)
        }
        mFavouriteCompanies?.add(company)
        mFavouriteCompaniesState.value.also {
            if (it is DataNotificator.Success) {
                it.data.add(company)
            }
        }
        mFavouriteCompaniesUpdateState.emit(DataNotificator.NewItem(company))
        mLocalInteractorHelper.updateCompany(company)
    }

    suspend fun onRemoveFavouriteCompany(company: AdaptiveCompany) {
        company.apply {
            isFavourite = false
            companyStyle.favouriteIconResource = mDataStylesManager.getIconDrawable(false)
        }
        mFavouriteCompanies?.remove(company)
        mFavouriteCompaniesState.value.also {
            if (it is DataNotificator.Success) {
                it.data.remove(company)
            }
        }
        mFavouriteCompaniesUpdateState.emit(DataNotificator.Remove(company))
        mLocalInteractorHelper.updateCompany(company)
    }

    suspend fun onStockCandlesLoaded(response: RepositoryResponse.Success<AdaptiveCompanyHistory>) : AdaptiveCompany? {
        val responseData = response.data
        val companyToUpdate = findCompany(response.owner)
        companyToUpdate?.let {
            it.companyHistory.apply {
                openPrices = responseData.openPrices
                highPrices = responseData.highPrices
                lowPrices = responseData.lowPrices
                closePrices = responseData.closePrices
                datePrices = responseData.datePrices
            }
            mLocalInteractorHelper.updateCompany(it)
        }
        return companyToUpdate
    }

    suspend fun onCompanyNewsLoaded(response: RepositoryResponse.Success<AdaptiveCompanyNews>) : AdaptiveCompany? {
        val responseData = response.data
        val companyToUpdate = findCompany(response.owner)
        companyToUpdate?.let {
            it.companyNews.apply {
                dates = responseData.dates
                headlines = responseData.headlines
                ids = responseData.ids
                previewImagesUrls = responseData.previewImagesUrls
                sources = responseData.sources
                summaries = responseData.summaries
                urls = responseData.urls
            }
            mLocalInteractorHelper.updateCompany(it)
        }
        return companyToUpdate
    }

    suspend fun onCompanyQuoteLoaded(response: RepositoryResponse.Success<AdaptiveCompanyDayData>) : AdaptiveCompany? {
        val responseData = response.data
        val companyToUpdate = findCompany(response.owner)
        companyToUpdate?.let {
            it.companyDayData.apply {
                openPrice = responseData.openPrice
                highPrice = responseData.highPrice
                lowPrice = responseData.lowPrice
                currentPrice = responseData.currentPrice
                previousClosePrice = responseData.previousClosePrice
                profit = responseData.profit
            }
            it.companyStyle.dayProfitBackground =
                mDataStylesManager.getProfitBackground(it.companyDayData.profit)
            mLocalInteractorHelper.updateCompany(it)
        }
        return companyToUpdate
    }

    suspend fun onWebSocketResponse(response: RepositoryResponse.Success<AdaptiveWebSocketPrice>): AdaptiveCompany? {
        val companyToUpdate = findCompany(response.owner)
        companyToUpdate?.let {
            it.companyDayData.apply {
                currentPrice = response.data.price
                profit = response.data.profit
            }
            it.companyStyle.dayProfitBackground =
                mDataStylesManager.getProfitBackground(it.companyDayData.profit)
            mLocalInteractorHelper.updateCompany(it)
        }
        return companyToUpdate
    }

    private fun findCompany(symbol: String?): AdaptiveCompany? {
        return mCompanies?.find { it.companyProfile.symbol == symbol }
    }
}