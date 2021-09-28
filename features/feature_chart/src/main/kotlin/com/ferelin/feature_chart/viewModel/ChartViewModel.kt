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
import com.ferelin.domain.entities.LiveTimePrice
import com.ferelin.domain.interactors.PastPriceInteractor
import com.ferelin.domain.interactors.PastPriceState
import com.ferelin.domain.interactors.StockPriceInteractor
import com.ferelin.domain.interactors.StockPriceState
import com.ferelin.domain.interactors.livePrice.LiveTimePriceInteractor
import com.ferelin.feature_chart.viewData.PastPriceLoadState
import com.ferelin.feature_chart.viewData.StockPriceLoadState
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class ChartViewModel @Inject constructor(
    private val mPastPriceInteractor: PastPriceInteractor,
    private val mLiveTimePriceInteractor: LiveTimePriceInteractor,
    private val mStockPriceInteractor: StockPriceInteractor,
    private val mDispatchersProvider: DispatchersProvider
) : ViewModel() {

    private val mPastPriceLoad = MutableStateFlow<PastPriceLoadState>(PastPriceLoadState.None)
    val pastPriceLoadState: StateFlow<PastPriceLoadState>
        get() = mPastPriceLoad.asStateFlow()

    private val mStockPriceLoad = MutableStateFlow<StockPriceLoadState>(StockPriceLoadState.None)
    val stockPriceLoad: StateFlow<StockPriceLoadState>
        get() = mStockPriceLoad.asStateFlow()

    fun loadPastPrices(companyId: Int, companyTicker: String) {
        viewModelScope.launch(mDispatchersProvider.IO) {
            mPastPriceLoad.value = PastPriceLoadState.Loading

            mPastPriceInteractor
                .getAllPastPrices(companyId)
                .ifNotEmpty { mPastPriceLoad.value = PastPriceLoadState.Loaded(it) }

            mPastPriceInteractor
                .loadPastPrices(companyTicker)
                .let { responseState ->
                    if (responseState is PastPriceState.Loaded) {
                        mPastPriceLoad.value = PastPriceLoadState.Loaded(responseState.pastPrices)
                    } else if (mPastPriceLoad.value !is PastPriceLoadState.Loaded) {
                        mPastPriceLoad.value = PastPriceLoadState.Error
                    }
                }
        }
    }

    fun loadActualStockPrice(companyId: Int) {
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

    fun observeLiveTimePrice(companyId: Int): SharedFlow<LiveTimePrice> {
        return mLiveTimePriceInteractor.observeLiveTimeUpdates()
            .filter { it != null && it.companyId == companyId }
            .map { it!! }
            .shareIn(viewModelScope, SharingStarted.WhileSubscribed())
    }
}