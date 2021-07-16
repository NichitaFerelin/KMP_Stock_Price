package com.ferelin.stockprice.ui.aboutSection.chart

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

import androidx.lifecycle.viewModelScope
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.adaptiveModels.AdaptiveCompanyHistoryForChart
import com.ferelin.stockprice.base.BaseViewModel
import com.ferelin.stockprice.custom.utils.Marker
import com.ferelin.stockprice.utils.DataNotificator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ChartViewModel(val selectedCompany: AdaptiveCompany) : BaseViewModel() {

    /*
    * There are different modes of displaying data.
    * For each of them, need to convert the data. This is the original list that can be used for it.
    */
    private var mOriginalStockHistory: AdaptiveCompanyHistoryForChart? = null

    private val mStateStockHistory =
        MutableStateFlow<DataNotificator<AdaptiveCompanyHistoryForChart>>(DataNotificator.None())
    val stateStockHistory: StateFlow<DataNotificator<AdaptiveCompanyHistoryForChart>>
        get() = mStateStockHistory

    val eventOnDayDataChanged: Flow<DataNotificator<AdaptiveCompany>>
        get() = mDataInteractor.sharedCompaniesUpdates.filter { filterSharedUpdate(it) }

    var chartViewMode: ChartViewMode = ChartViewMode.All

    var lastClickedMarker: Marker? = null

    val eventOnError: SharedFlow<String>
        get() = mDataInteractor.sharedLoadStockCandlesError

    val stockPrice: String
        get() = selectedCompany.companyDayData.currentPrice

    val dayProfit: String
        get() = selectedCompany.companyDayData.profit

    val profitBackgroundResource: Int
        get() = selectedCompany.companyStyle.dayProfitBackground

    val isHistoryEmpty: Boolean
        get() = selectedCompany.companyHistory.datePrices.isEmpty()

    override fun initObserversBlock() {
        viewModelScope.launch(mCoroutineContext.IO) {
            prepareInitialChartState()
            loadStockHistory()
        }
    }

    fun onChartControlButtonClicked(selectedViewMode: ChartViewMode) {
        lastClickedMarker = null
        chartViewMode = selectedViewMode
        mOriginalStockHistory?.let { convertHistoryToSelectedType(selectedViewMode) }
    }

    private fun prepareInitialChartState() {
        if (isHistoryEmpty) {
            return
        }

        onStockHistoryLoaded()
        mStateStockHistory.value = DataNotificator.DataPrepared(mOriginalStockHistory!!)
    }

    private suspend fun loadStockHistory() {
        mDataInteractor.loadStockHistory(selectedCompany.companyProfile.symbol)
            .collect { notificator ->
                when (notificator) {
                    is DataNotificator.DataPrepared -> {
                        onStockHistoryLoaded()
                        mStateStockHistory.value =
                            DataNotificator.DataPrepared(mOriginalStockHistory!!)
                    }
                    is DataNotificator.Loading -> {
                        if (mStateStockHistory.value !is DataNotificator.DataPrepared) {
                            mStateStockHistory.value = DataNotificator.Loading()
                        }
                    }
                    else -> {
                        if (mStateStockHistory.value !is DataNotificator.DataPrepared) {
                            mStateStockHistory.value = DataNotificator.None()
                        }
                    }
                }
            }
    }

    private fun onStockHistoryLoaded() {
        val adaptiveHistory = mDataInteractor.stockHistoryConverter
            .toCompanyHistoryForChart(selectedCompany.companyHistory)
        mOriginalStockHistory = adaptiveHistory
    }

    private fun convertHistoryToSelectedType(selectedViewMode: ChartViewMode) {
        viewModelScope.launch(mCoroutineContext.IO) {
            with(mDataInteractor.stockHistoryConverter) {
                val convertedHistory = when (selectedViewMode) {
                    is ChartViewMode.SixMonths -> toSixMonths(mOriginalStockHistory!!)
                    is ChartViewMode.Months -> toMonths(mOriginalStockHistory!!)
                    is ChartViewMode.Weeks -> toWeeks(mOriginalStockHistory!!)
                    is ChartViewMode.Year -> toOneYear(mOriginalStockHistory!!)
                    is ChartViewMode.All -> mOriginalStockHistory
                    is ChartViewMode.Days -> mOriginalStockHistory
                }
                mStateStockHistory.value = DataNotificator.DataPrepared(convertedHistory!!)
            }
        }
    }

    private fun isNewData(loadedHistory: AdaptiveCompanyHistoryForChart): Boolean {
        return loadedHistory != mOriginalStockHistory
    }

    private fun filterSharedUpdate(notificator: DataNotificator<AdaptiveCompany>): Boolean {
        return (notificator is DataNotificator.ItemUpdatedLiveTime
                || notificator is DataNotificator.ItemUpdatedQuote)
                && notificator.data != null
                && notificator.data.companyProfile.symbol == selectedCompany.companyProfile.symbol
    }
}