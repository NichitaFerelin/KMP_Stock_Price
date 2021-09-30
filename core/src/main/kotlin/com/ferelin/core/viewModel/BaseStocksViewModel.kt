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
import com.ferelin.core.utils.LoadState
import com.ferelin.core.viewData.StockViewData
import com.ferelin.domain.entities.Company
import com.ferelin.domain.entities.CompanyWithStockPrice
import com.ferelin.domain.interactors.StockPriceInteractor
import com.ferelin.domain.interactors.companies.CompaniesInteractor
import com.ferelin.domain.interactors.livePrice.LiveTimePriceInteractor
import com.ferelin.navigation.Router
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

typealias ViewData = List<StockViewData>

abstract class BaseStocksViewModel(
    private val mStockMapper: StockMapper,
    private val mRouter: Router,
    protected val mCompaniesInteractor: CompaniesInteractor,
    protected val mStockPriceInteractor: StockPriceInteractor,
    protected val mDispatchesProvider: DispatchersProvider
) : ViewModel() {

    private val mStocksLoadState = MutableStateFlow<LoadState<ViewData>>(LoadState.None())
    val stocksLoadState: StateFlow<LoadState<ViewData>>
        get() = mStocksLoadState.asStateFlow()

    val companiesStockPriceUpdates: SharedFlow<CompanyWithStockPrice>
        get() = mCompaniesInteractor.companyWithStockPriceChanges

    val favouriteCompaniesUpdates: SharedFlow<Company>
        get() = mCompaniesInteractor.observeFavouriteCompaniesUpdates()

    fun loadStocks(onlyFavourites: Boolean = false) {
        viewModelScope.launch(mDispatchesProvider.IO) {
            mStocksLoadState.value = LoadState.Loading()

            val companies = if (onlyFavourites) {
                mCompaniesInteractor.getAll()
            } else {
                mCompaniesInteractor.getAllFavourites()
            }

            mStocksLoadState.value = LoadState.Prepared(
                data = companies.map(mStockMapper::map)
            )
        }
    }

    fun onStockClick(stockViewData: StockViewData) {
        // replace
    }

    fun onFavouriteIconClick(stockViewData: StockViewData) {
        viewModelScope.launch(mDispatchesProvider.IO) {
            val company = mStockMapper.map(stockViewData)

            if (company.isFavourite) {
                mCompaniesInteractor.addCompanyToFavourites(company)
            } else {
                mCompaniesInteractor.removeCompanyFromFavourites(company)
            }
        }
    }

    fun onBind(stockViewData: StockViewData) {
        viewModelScope.launch(mDispatchesProvider.IO) {
            mStockPriceInteractor.addRequestToGetStockPrice(
                stockViewData.ticker,
                stockViewData.id
            )
        }
    }
}