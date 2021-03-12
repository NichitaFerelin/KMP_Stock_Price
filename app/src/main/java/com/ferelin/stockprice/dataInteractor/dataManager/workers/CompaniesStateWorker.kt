package com.ferelin.stockprice.dataInteractor.dataManager.workers

import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.dataInteractor.dataManager.StylesProvider
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorHelper
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.NULL_INDEX
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class CompaniesStateWorker(
    private val mStylesProvider: StylesProvider,
    private val mLocalInteractorHelper: LocalInteractorHelper
) {
    private var mCompanies: ArrayList<AdaptiveCompany> = arrayListOf()

    private val mCompaniesState =
        MutableStateFlow<DataNotificator<List<AdaptiveCompany>>>(DataNotificator.Loading())
    val companiesState: StateFlow<DataNotificator<List<AdaptiveCompany>>>
        get() = mCompaniesState

    private val mCompaniesUpdatesShared = MutableSharedFlow<DataNotificator<AdaptiveCompany>>()
    val companiesUpdatesShared: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mCompaniesUpdatesShared

    fun onDataPrepared(companies: List<AdaptiveCompany>) {
        companies.forEachIndexed { index, company ->
            mStylesProvider.applyStyles(company, index)
        }
        mCompanies = ArrayList(companies)
        mCompaniesState.value = DataNotificator.DataPrepared(companies)
    }

    suspend fun onCompanyChanged(notification: DataNotificator<AdaptiveCompany>) {
        val companyIndex = mCompanies.indexOf(notification.data)
        if (companyIndex != NULL_INDEX) {
            mCompanies[companyIndex] = notification.data!!
            mCompaniesState.value = DataNotificator.DataPrepared(mCompanies)
            mCompaniesUpdatesShared.emit(notification)
        }
    }

    suspend fun onStockCandlesLoaded(response: RepositoryResponse.Success<AdaptiveCompanyHistory>): AdaptiveCompany? {
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

    suspend fun onCompanyNewsLoaded(response: RepositoryResponse.Success<AdaptiveCompanyNews>): AdaptiveCompany? {
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

    suspend fun onCompanyQuoteLoaded(response: RepositoryResponse.Success<AdaptiveCompanyDayData>): AdaptiveCompany? {
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
                mStylesProvider.getProfitBackground(it.companyDayData.profit)
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
                mStylesProvider.getProfitBackground(it.companyDayData.profit)
            mLocalInteractorHelper.updateCompany(it)
        }
        return companyToUpdate
    }

    fun findCompany(symbol: String?): AdaptiveCompany? {
        return mCompanies.find { it.companyProfile.symbol == symbol }
    }
}