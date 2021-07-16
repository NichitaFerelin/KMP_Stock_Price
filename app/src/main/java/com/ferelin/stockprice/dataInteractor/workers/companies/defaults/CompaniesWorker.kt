package com.ferelin.stockprice.dataInteractor.workers.companies.defaults

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
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveWebSocketPrice
import com.ferelin.stockprice.dataInteractor.utils.CompanyStyleProvider
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [CompaniesWorker] provides an ability to:
 *   - Observing [mStateCompanies] to display a list of companies.
 *   - Observing [mSharedCompaniesUpdates] to update items at list.
 *
 * Also [CompaniesWorker] manually doing:
 *   - Using [mRepository] to data caching.
 *   - Using [mCompanyStyleProvider] to change some stock fields that will be affect on stock's appearance.
 */

@Singleton
class CompaniesWorker @Inject constructor(
    private val mRepository: Repository,
    private val mAppScope: CoroutineScope,
    private val mCompanyStyleProvider: CompanyStyleProvider,
) : CompaniesWorkerStates {

    private var mCompanies: ArrayList<AdaptiveCompany> = arrayListOf()

    private val mStateCompanies =
        MutableStateFlow<DataNotificator<List<AdaptiveCompany>>>(DataNotificator.None())
    override val stateCompanies: StateFlow<DataNotificator<List<AdaptiveCompany>>>
        get() = mStateCompanies

    private val mSharedCompaniesUpdates = MutableSharedFlow<DataNotificator<AdaptiveCompany>>()
    override val sharedCompaniesUpdates: SharedFlow<DataNotificator<AdaptiveCompany>>
        get() = mSharedCompaniesUpdates

    fun onCompaniesDataPrepared(companies: List<AdaptiveCompany>) {
        companies.forEachIndexed { index, company ->
            mCompanyStyleProvider.applyStyle(company, index)
        }
        mCompanies = ArrayList(companies)
        mStateCompanies.value = DataNotificator.DataPrepared(mCompanies)
    }

    fun onLiveTimePriceChanged(company: AdaptiveCompany, newData: AdaptiveWebSocketPrice) {
        company.apply {
            companyDayData.currentPrice = newData.price
            companyDayData.profit = newData.profit
            companyStyle.dayProfitBackground =
                mCompanyStyleProvider.getProfitBackground(newData.profit)
        }
        mAppScope.launch { mRepository.cacheCompanyToLocalDb(company) }
    }

    fun isDataChanged(
        symbolCompanyOwner: String,
        compareStrategy: (AdaptiveCompany) -> Boolean
    ): AdaptiveCompany? {
        return mCompanies
            .find { it.companyProfile.symbol == symbolCompanyOwner }
            ?.let { targetCompany ->
                if (compareStrategy.invoke(targetCompany)) {
                    null
                } else targetCompany
            }
    }

    suspend fun onCompanyChanged(notification: DataNotificator<AdaptiveCompany>) {
        mAppScope.launch { mRepository.cacheCompanyToLocalDb(notification.data!!) }
        mSharedCompaniesUpdates.emit(notification)
    }
}