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

package com.ferelin.core.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.mapper.StockMapper
import com.ferelin.core.viewData.StockViewData
import com.ferelin.domain.entities.Company
import com.ferelin.domain.entities.LiveTimePrice
import com.ferelin.domain.interactors.StockPriceInteractor
import com.ferelin.domain.interactors.companies.CompaniesInteractor
import com.ferelin.domain.interactors.livePrice.LiveTimePriceInteractor
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class StocksLoadState {
    class Loaded(val items: List<StockViewData>) : StocksLoadState()
    object Loading : StocksLoadState()
    object None : StocksLoadState()
}

abstract class BaseStocksViewModel(
    private val mStockMapper: StockMapper,
    protected val mCompaniesInteractor: CompaniesInteractor,
    protected val mStockPriceInteractor: StockPriceInteractor,
    protected val mLiveTimePriceInteractor: LiveTimePriceInteractor,
    protected val mDispatchesProvider: DispatchersProvider
) : ViewModel() {

    private val mStocksLoadState = MutableStateFlow<StocksLoadState>(StocksLoadState.None)
    val stocksLoadState: StateFlow<StocksLoadState>
        get() = mStocksLoadState.asStateFlow()

    val favouriteCompaniesUpdates: SharedFlow<Company>
        get() = mCompaniesInteractor.observeFavouriteCompaniesUpdates()

    val liveTimePrice: SharedFlow<LiveTimePrice> = mLiveTimePriceInteractor
        .observeLiveTimeUpdates()
        .filter { it != null }
        .map { it!! }
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(5000L))

    fun loadStocks(onlyFavourites: Boolean = false) {
        viewModelScope.launch(mDispatchesProvider.IO) {
            mStocksLoadState.value = StocksLoadState.Loading

            val companies = if (onlyFavourites) {
                mCompaniesInteractor.getAll()
            } else {
                mCompaniesInteractor.getAllFavourites()
            }

            mStocksLoadState.value = StocksLoadState.Loaded(
                items = companies.map(mStockMapper::map)
            )
        }
    }

    fun onBind(item: StockViewData) {
        viewModelScope.launch(mDispatchesProvider.IO) {
            mStocksLoadState.let { stocksLoadState ->
                if (stocksLoadState.value !is StocksLoadState.Loaded) {
                    return@launch
                }

                mStockPriceInteractor.addRequestToGetStockPrice(
                    item.ticker,
                    item.id
                )
            }
        }
    }
}