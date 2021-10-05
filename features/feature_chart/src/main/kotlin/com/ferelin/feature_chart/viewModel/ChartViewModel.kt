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

package com.ferelin.feature_chart.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.utils.ifNotEmpty
import com.ferelin.domain.entities.CompanyWithStockPrice
import com.ferelin.domain.entities.PastPrice
import com.ferelin.domain.interactors.PastPriceInteractor
import com.ferelin.domain.interactors.PastPriceState
import com.ferelin.domain.interactors.StockPriceInteractor
import com.ferelin.domain.interactors.StockPriceState
import com.ferelin.domain.interactors.companies.CompaniesInteractor
import com.ferelin.feature_chart.mapper.PastPriceTypeMapper
import com.ferelin.feature_chart.utils.points.Marker
import com.ferelin.feature_chart.viewData.ChartViewMode
import com.ferelin.feature_chart.viewData.PastPriceLoadState
import com.ferelin.feature_chart.viewData.StockPriceLoadState
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChartViewModel @Inject constructor(
    private val mCompaniesInteractor: CompaniesInteractor,
    private val mPastPriceInteractor: PastPriceInteractor,
    private val mStockPriceInteractor: StockPriceInteractor,
    private val mPastPriceTypeMapper: PastPriceTypeMapper,
    private val mDispatchersProvider: DispatchersProvider
) : ViewModel() {

    private var mPastPrices: List<PastPrice> = emptyList()

    private val mPastPriceLoad = MutableStateFlow<PastPriceLoadState>(PastPriceLoadState.None)
    val pastPriceLoadState: StateFlow<PastPriceLoadState>
        get() = mPastPriceLoad.asStateFlow()

    private val mStockPriceLoad = MutableStateFlow<StockPriceLoadState>(StockPriceLoadState.None)
    val stockPriceLoad: StateFlow<StockPriceLoadState>
        get() = mStockPriceLoad.asStateFlow()

    val companiesStockPriceUpdates: SharedFlow<CompanyWithStockPrice>
        get() = mCompaniesInteractor.companyWithStockPriceChanges

    var chartMode: ChartViewMode = ChartViewMode.All
        private set

    var selectedMarker: Marker? = null
    var companyId: Int = 0
    var companyTicker = ""

    fun loadPastPrices() {
        viewModelScope.launch(mDispatchersProvider.IO) {
            mPastPriceLoad.value = PastPriceLoadState.Loading

            mPastPriceInteractor
                .getAllPastPrices(companyId)
                .ifNotEmpty { dbPrices -> onNewsPastPrices(dbPrices) }

            mPastPriceInteractor
                .loadPastPrices(companyTicker)
                .let { responseState ->
                    if (responseState is PastPriceState.Loaded) {
                        onNewsPastPrices(responseState.pastPrices)
                    } else if (mPastPriceLoad.value !is PastPriceLoadState.Loaded) {
                        mPastPriceLoad.value = PastPriceLoadState.Error
                    }
                }
        }
    }

    fun loadActualStockPrice() {
        viewModelScope.launch(mDispatchersProvider.IO) {
            mStockPriceLoad.value = StockPriceLoadState.Loading

            mStockPriceInteractor
                .observeActualStockPriceResponses()
                .filter { it is StockPriceState.Loaded && it.stockPrice.id == companyId }
                .take(1)
                .collect {
                    mStockPriceLoad.value = StockPriceLoadState.Loaded(
                        stockPrice = (it as StockPriceState.Loaded).stockPrice
                    )
                }
        }
    }

    fun onNewChartMode(chartViewMode: ChartViewMode) {
        this.chartMode = chartViewMode
        onNewsPastPrices(mPastPrices)
    }

    private fun onNewsPastPrices(pastPrices: List<PastPrice>) {
        mPastPrices = pastPrices

        mPastPriceTypeMapper
            .mapByViewMode(chartMode, pastPrices)
            ?.let { chartPastPrices ->
                mPastPriceLoad.value = PastPriceLoadState.Loaded(chartPastPrices)
            }
    }
}