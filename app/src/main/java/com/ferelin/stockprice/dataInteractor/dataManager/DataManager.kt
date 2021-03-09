package com.ferelin.stockprice.dataInteractor.dataManager

import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utilits.RepositoryResponse
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorHelper
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DataManager(
    private val mLocalInteractorHelper: LocalInteractorHelper,
    private val mDataStylesManager: DataStylesManager
) {
    private var mCompanies: List<AdaptiveCompany>? = null
    private var mFavouriteCompanies: MutableList<AdaptiveCompany>? = null
    private var mSearchedRequests: MutableList<AdaptiveSearchRequest>? = null

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

    private var mSearchedRequestsState =
        MutableStateFlow<DataNotificator<MutableList<AdaptiveSearchRequest>>>(DataNotificator.Loading())
    val searchedRequestsState: StateFlow<DataNotificator<MutableList<AdaptiveSearchRequest>>>
        get() = mSearchedRequestsState

    fun onCompaniesDataPrepared(companies: List<AdaptiveCompany>) {
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

    fun onSearchesDataPrepared(searches: List<AdaptiveSearchRequest>) {
        val searchesHistory = searches.toMutableList()
        mSearchedRequests = searchesHistory
        mSearchedRequestsState.value = DataNotificator.Success(searchesHistory)
    }

    suspend fun onStockCandlesLoaded(response: RepositoryResponse.Success<AdaptiveStockCandles>) {
        val responseData = response.data
        val companyToUpdate = mCompanies?.find { it.symbol == responseData.symbol }
        companyToUpdate?.let {
            responseData.company = it
            it.apply {
                historyOpenPrices = responseData.openPrices
                historyHighPrices = responseData.highPrices
                historyLowPrices = responseData.lowPrices
                historyClosePrices = responseData.closePrices
                historyTimestampsPrices = responseData.timestamps
            }
            mLocalInteractorHelper.updateCompany(it)
        }
    }

    suspend fun onCompanyNewsLoaded(response: RepositoryResponse.Success<AdaptiveCompanyNews>) {
        val responseData = response.data
        val companyToUpdate = mCompanies?.find { it.symbol == responseData.symbol }
        companyToUpdate?.let {
            it.apply {
                newsTimestamps = responseData.date
                newsHeadline = responseData.headline
                newsIds = responseData.newsId
                newsImages = responseData.previewImageUrl
                newsSource = responseData.source
                newsSummary = responseData.summary
                newsUrl = responseData.url
            }
            mLocalInteractorHelper.updateCompany(it)
        }
    }

    suspend fun onCompanyQuoteLoaded(response: RepositoryResponse.Success<AdaptiveCompanyQuote>) {
        val responseData = response.data
        val companyToUpdate = mCompanies?.find { it.symbol == responseData.symbol }
        companyToUpdate?.let {
            responseData.company = it
            it.apply {
                dayOpenPrice = responseData.openPrice
                dayHighPrice = responseData.highPrice
                dayLowPrice = responseData.lowPrice
                dayCurrentPrice = responseData.currentPrice
                dayPreviousClosePrice = responseData.previousClosePrice
                dayProfit = responseData.dayDelta
                dayProfitBackground = mDataStylesManager.getProfitBackground(dayProfit)
            }
            mLocalInteractorHelper.updateCompany(it)
        }
    }

    suspend fun onAddFavouriteCompany(company: AdaptiveCompany) {
        company.apply {
            isFavourite = true
            favouriteIconDrawable = mDataStylesManager.getIconDrawable(isFavourite)
        }
        mFavouriteCompanies?.add(company)
        mFavouriteCompaniesState.value.also {
            if (it is DataNotificator.Success) {
                it.data.add(company)
            }
        }
        mLocalInteractorHelper.updateCompany(company)
    }

    suspend fun onRemoveFavouriteCompany(company: AdaptiveCompany) {
        company.apply {
            isFavourite = false
            favouriteIconDrawable = mDataStylesManager.getIconDrawable(isFavourite)
        }
        mFavouriteCompanies?.remove(company)
        mFavouriteCompaniesState.value.also {
            if (it is DataNotificator.Success) {
                it.data.remove(company)
            }
        }
        mLocalInteractorHelper.updateCompany(company)
    }

    fun onWebSocketResponse(response: RepositoryResponse.Success<AdaptiveLastPrice>) {
        val companyToUpdate = mCompanies?.find { it.symbol == response.data.symbol }
        companyToUpdate?.apply {
            dayCurrentPrice = response.data.lastPrice
        }
    }
}