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

/**
 * [CompaniesWorker] providing an ability to:
 *   - Observing [mStateCompanies] to display a list of companies.
 *   - Observing [mSharedCompaniesUpdates] to update items at list.
 *
 * Also [CompaniesWorker] manually doing:
 *   - Using [mLocalInteractorHelper] to data caching.
 *   - Using [mStylesProvider] to change some stock fields that will be affect on stock's appearance.
 */
class CompaniesWorker(
    private val mStylesProvider: StylesProvider,
    private val mLocalInteractorHelper: LocalInteractorHelper
) {
    private var mCompanies: ArrayList<AdaptiveCompany> = arrayListOf()
    val companies: ArrayList<AdaptiveCompany>
        get() = mCompanies

    private val mStateCompanies = MutableStateFlow<DataNotificator<ArrayList<AdaptiveCompany>>>(
        DataNotificator.Loading()
    )
    val stateCompanies: StateFlow<DataNotificator<ArrayList<AdaptiveCompany>>>
        get() = mStateCompanies

    private val mSharedCompaniesUpdates = MutableSharedFlow<DataNotificator<AdaptiveCompany>>()
    val sharedCompaniesUpdates: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mSharedCompaniesUpdates

    fun onDataPrepared(companies: List<AdaptiveCompany>) {
        companies.forEachIndexed { index, company -> mStylesProvider.applyStyles(company, index) }
        mCompanies = ArrayList(companies)
        mStateCompanies.value = DataNotificator.DataPrepared(mCompanies)
    }

    suspend fun onCompanyChanged(notification: DataNotificator<AdaptiveCompany>) {
        mSharedCompaniesUpdates.emit(notification)
    }

    suspend fun onStockCandlesLoaded(response: RepositoryResponse.Success<AdaptiveCompanyHistory>): AdaptiveCompany? {
        return onDataChanged(
            responseOwner = response.owner,
            isDataNew = { it.companyHistory == response.data },
            onApply = { companyToUpdate ->
                companyToUpdate.companyHistory = response.data
                mLocalInteractorHelper.cacheCompany(companyToUpdate)
            }
        )
    }

    suspend fun onCompanyNewsLoaded(response: RepositoryResponse.Success<AdaptiveCompanyNews>): AdaptiveCompany? {
        return onDataChanged(
            responseOwner = response.owner,
            isDataNew = { it.companyNews == response.data },
            onApply = { companyToUpdate ->
                companyToUpdate.companyNews = response.data
                mLocalInteractorHelper.cacheCompany(companyToUpdate)
            }
        )
    }

    suspend fun onCompanyQuoteLoaded(response: RepositoryResponse.Success<AdaptiveCompanyDayData>): AdaptiveCompany? {
        return onDataChanged(
            responseOwner = response.owner,
            isDataNew = { it.companyDayData == response.data },
            onApply = { companyToUpdate ->
                companyToUpdate.companyDayData = response.data
                companyToUpdate.companyStyle.dayProfitBackground =
                    mStylesProvider.getProfitBackground(companyToUpdate.companyDayData.profit)

                mLocalInteractorHelper.cacheCompany(companyToUpdate)
            }
        )
    }

    suspend fun onLiveTimePriceChanged(response: RepositoryResponse.Success<AdaptiveWebSocketPrice>): AdaptiveCompany? {
        return onDataChanged(
            responseOwner = response.owner,
            isDataNew = { it.companyDayData.profit == response.data.price },
            onApply = { companyToUpdate ->
                companyToUpdate.companyDayData.currentPrice = response.data.price
                companyToUpdate.companyDayData.profit = response.data.profit
                companyToUpdate.companyStyle.dayProfitBackground =
                    mStylesProvider.getProfitBackground(companyToUpdate.companyDayData.profit)

                mLocalInteractorHelper.cacheCompany(companyToUpdate)
            }
        )
    }

    private inline fun onDataChanged(
        responseOwner: String?,
        isDataNew: (AdaptiveCompany) -> Boolean,
        onApply: (AdaptiveCompany) -> Unit
    ): AdaptiveCompany? {
        return findCompany(mCompanies, responseOwner)?.let { companyToUpdate ->
            // Response data that is equal to original makes no sense.
            if (isDataNew.invoke(companyToUpdate)) {
                null
            } else {
                onApply.invoke(companyToUpdate)
                companyToUpdate
            }
        }
    }
}