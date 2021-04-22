package com.ferelin.stockprice.dataInteractor.dataManager.workers

import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.dataInteractor.dataManager.StylesProvider
import com.ferelin.stockprice.dataInteractor.local.LocalInteractorHelper
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.findCompany
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

/*
* Worker that is responsible for:
*   - Providing companies(items) for display.
*   - Providing company news(items) for display.
*   - Notification when company data is updated(Quote changes / LiveTimePrice / StockCandles updates).
*   - Companies data caching.
* */
class CompaniesStateWorker(
    private val mStylesProvider: StylesProvider,
    private val mLocalInteractorHelper: LocalInteractorHelper
) {
    private var mCompanies: ArrayList<AdaptiveCompany> = arrayListOf()
    val companies: ArrayList<AdaptiveCompany>
        get() = mCompanies

    private val mCompaniesState = MutableStateFlow<DataNotificator<List<AdaptiveCompany>>>(
        DataNotificator.Loading()
    )
    val companiesState: StateFlow<DataNotificator<List<AdaptiveCompany>>>
        get() = mCompaniesState

    private val mCompaniesUpdatesShared = MutableSharedFlow<DataNotificator<AdaptiveCompany>>()
    val companiesUpdatesShared: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mCompaniesUpdatesShared

    fun onDataPrepared(companies: List<AdaptiveCompany>) {
        companies.forEachIndexed { index, company -> mStylesProvider.applyStyles(company, index) }
        mCompanies = ArrayList(companies)
        mCompaniesState.value = DataNotificator.DataPrepared(companies)
    }

    suspend fun onCompanyChanged(notification: DataNotificator<AdaptiveCompany>) {
        mCompaniesUpdatesShared.emit(notification)
    }

    suspend fun onStockCandlesLoaded(response: RepositoryResponse.Success<AdaptiveCompanyHistory>): AdaptiveCompany? {
        val companyToUpdate = findCompany(mCompanies, response.owner)
        companyToUpdate?.let {
            it.companyHistory = response.data
            mLocalInteractorHelper.updateCompany(it)
        }
        return companyToUpdate
    }

    suspend fun onCompanyNewsLoaded(response: RepositoryResponse.Success<AdaptiveCompanyNews>): AdaptiveCompany? {
        val responseData = response.data
        val companyToUpdate = findCompany(mCompanies, response.owner)
        companyToUpdate?.let {
            it.companyNews = responseData
            mLocalInteractorHelper.updateCompany(it)
        }
        return companyToUpdate
    }

    suspend fun onCompanyQuoteLoaded(response: RepositoryResponse.Success<AdaptiveCompanyDayData>): AdaptiveCompany? {
        val responseData = response.data
        val companyToUpdate = findCompany(mCompanies, response.owner)
        return if (companyToUpdate != null && companyToUpdate.companyDayData != responseData) {
            companyToUpdate.apply {
                companyDayData = responseData
                companyStyle.dayProfitBackground =
                    mStylesProvider.getProfitBackground(companyToUpdate.companyDayData.profit)
            }
            mLocalInteractorHelper.updateCompany(companyToUpdate)
            companyToUpdate
        } else null
    }

    suspend fun onWebSocketResponse(response: RepositoryResponse.Success<AdaptiveWebSocketPrice>): AdaptiveCompany? {
        val companyToUpdate = findCompany(mCompanies, response.owner)
        companyToUpdate?.let {
            it.companyDayData.currentPrice = response.data.price
            it.companyDayData.profit = response.data.profit
            it.companyStyle.dayProfitBackground =
                mStylesProvider.getProfitBackground(it.companyDayData.profit)
            mLocalInteractorHelper.updateCompany(it)
        }
        return companyToUpdate
    }
}