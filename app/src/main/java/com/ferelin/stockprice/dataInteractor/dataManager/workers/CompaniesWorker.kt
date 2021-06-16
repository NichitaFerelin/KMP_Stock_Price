package com.ferelin.stockprice.dataInteractor.dataManager.workers

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

import com.ferelin.repository.Repository
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.stockprice.dataInteractor.dataManager.StylesProvider
import com.ferelin.stockprice.utils.DataNotificator
import com.ferelin.stockprice.utils.findCompany
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [CompaniesWorker] provides an ability to:
 *   - Observing [mStateCompanies] to display a list of companies.
 *   - Observing [mSharedCompaniesUpdates] to update items at list.
 *
 * Also [CompaniesWorker] manually doing:
 *   - Using [mRepository] to data caching.
 *   - Using [mStylesProvider] to change some stock fields that will be affect on stock's appearance.
 */

@Singleton
class CompaniesWorker @Inject constructor(
    private val mStylesProvider: StylesProvider,
    private val mRepository: Repository
) {
    private var mCompanies: ArrayList<AdaptiveCompany> = arrayListOf()
    val companies: List<AdaptiveCompany>
        get() = mCompanies.toList()

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

    fun onStockCandlesLoaded(response: RepositoryResponse.Success<AdaptiveCompanyHistory>): AdaptiveCompany? {
        return onDataChanged(
            responseOwner = response.owner,
            isDataNew = { it.companyHistory == response.data },
            onApply = { companyToUpdate ->
                companyToUpdate.companyHistory = response.data
                mRepository.cacheCompany(companyToUpdate)
            }
        )
    }

    fun onCompanyNewsLoaded(response: RepositoryResponse.Success<AdaptiveCompanyNews>): AdaptiveCompany? {
        return onDataChanged(
            responseOwner = response.owner,
            isDataNew = { it.companyNews == response.data },
            onApply = { companyToUpdate ->
                companyToUpdate.companyNews = response.data
                mRepository.cacheCompany(companyToUpdate)
            }
        )
    }

    fun onCompanyQuoteLoaded(response: RepositoryResponse.Success<AdaptiveCompanyDayData>): AdaptiveCompany? {
        return onDataChanged(
            responseOwner = response.owner,
            isDataNew = { it.companyDayData == response.data },
            onApply = { companyToUpdate ->
                companyToUpdate.companyDayData = response.data
                companyToUpdate.companyStyle.dayProfitBackground =
                    mStylesProvider.getProfitBackground(companyToUpdate.companyDayData.profit)

                mRepository.cacheCompany(companyToUpdate)
            }
        )
    }

    fun onLiveTimePriceChanged(response: RepositoryResponse.Success<AdaptiveWebSocketPrice>): AdaptiveCompany? {
        return onDataChanged(
            responseOwner = response.owner,
            isDataNew = { it.companyDayData.profit == response.data.price },
            onApply = { companyToUpdate ->
                companyToUpdate.companyDayData.currentPrice = response.data.price
                companyToUpdate.companyDayData.profit = response.data.profit
                companyToUpdate.companyStyle.dayProfitBackground =
                    mStylesProvider.getProfitBackground(companyToUpdate.companyDayData.profit)

                mRepository.cacheCompany(companyToUpdate)
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