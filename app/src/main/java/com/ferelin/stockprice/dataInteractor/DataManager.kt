package com.ferelin.stockprice.dataInteractor

import android.util.Log
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveLastPrice
import com.ferelin.repository.adaptiveModels.AdaptiveSearch
import com.ferelin.repository.adaptiveModels.AdaptiveStockCandle
import com.ferelin.repository.utilits.RepositoryResponse
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorHelper
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class DataManager(private val mLocalInteractorHelper: LocalInteractorHelper) {

    private var mCompanies: List<AdaptiveCompany>? = null
    private var mFavouriteCompanies: MutableList<AdaptiveCompany>? = null
    private var mPopularRequests: List<AdaptiveSearch>? = null
    private var mSearchedRequests: MutableList<AdaptiveSearch>? = null

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

    private var mPopularRequestsState = MutableStateFlow<DataNotificator<List<AdaptiveSearch>>>(
        DataNotificator.Loading()
    )
    val popularRequestsState: StateFlow<DataNotificator<List<AdaptiveSearch>>>
        get() = mPopularRequestsState

    private var mSearchedRequestsState =
        MutableStateFlow<DataNotificator<MutableList<AdaptiveSearch>>>(DataNotificator.Loading())
    val searchedRequestsState: StateFlow<DataNotificator<MutableList<AdaptiveSearch>>>
        get() = mSearchedRequestsState

    fun onCompaniesDataPrepared(
        companies: List<AdaptiveCompany>,
        favouriteCompanies: List<AdaptiveCompany>
    ) {
        mCompanies = companies
        mFavouriteCompanies = favouriteCompanies.toMutableList()
        mCompaniesState.value = DataNotificator.Success(companies)
        mFavouriteCompaniesState.value = DataNotificator.Success(favouriteCompanies.toMutableList())
    }

    fun onSearchesDataPrepared(
        searches: List<AdaptiveSearch>,
        popularRequests: List<AdaptiveSearch>
    ) {
        Log.d("Test", "Prepare")
        val searchesHistory = searches.toMutableList()
        mSearchedRequests = searchesHistory
        mSearchedRequestsState.value = DataNotificator.Success(searchesHistory)

        mPopularRequests = popularRequests
        mPopularRequestsState.value = DataNotificator.Success(popularRequests)
    }

    suspend fun onStockCandlesLoaded(response: RepositoryResponse.Success<AdaptiveStockCandle>) {
        val responseData = response.data
        val companyToUpdate = mCompanies?.find { it.symbol == responseData.symbol }
        companyToUpdate?.let {
            responseData.company = it
            it.apply {
                openPrices = responseData.openPrices
                highPrices = responseData.highPrices
                lowPrices = responseData.lowPrices
                closePrices = responseData.closePrices
                timestamps = responseData.timestamps
                dayProfitPercents = responseData.dayProfit
            }
            mLocalInteractorHelper.updateCompany(it)
        }
    }

    suspend fun onAddFavouriteCompany(company: AdaptiveCompany) {
        company.apply {
            isFavourite = true
            favouriteIconBackground = DataInteractorHelper.DRAWABLE_FAVOURITE_ICON_ACTIVE
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
            favouriteIconBackground = DataInteractorHelper.DRAWABLE_FAVOURITE_ICON
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
            lastPrice = response.data.lastPrice
        }
    }
}