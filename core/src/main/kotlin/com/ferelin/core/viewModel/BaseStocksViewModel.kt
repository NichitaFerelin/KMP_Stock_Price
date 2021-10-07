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
import com.ferelin.core.adapter.base.BaseRecyclerAdapter
import com.ferelin.core.adapter.stocks.PAYLOAD_FAVOURITE_UPDATED
import com.ferelin.core.adapter.stocks.PAYLOAD_PRICE_UPDATED
import com.ferelin.core.adapter.stocks.createStocksAdapter
import com.ferelin.core.mapper.StockMapper
import com.ferelin.core.utils.LoadState
import com.ferelin.core.utils.SHARING_STOP_TIMEOUT
import com.ferelin.core.utils.ifPrepared
import com.ferelin.core.viewData.StockViewData
import com.ferelin.domain.entities.Company
import com.ferelin.domain.entities.CompanyWithStockPrice
import com.ferelin.domain.interactors.StockPriceInteractor
import com.ferelin.domain.interactors.StockPriceState
import com.ferelin.domain.interactors.companies.CompaniesInteractor
import com.ferelin.navigation.Router
import com.ferelin.shared.DispatchersProvider
import com.ferelin.shared.ifExist
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

typealias StockLoadState = LoadState<List<StockViewData>>

abstract class BaseStocksViewModel(
    private val mStockMapper: StockMapper,
    private val mRouter: Router,
    protected val mCompaniesInteractor: CompaniesInteractor,
    protected val mStockPriceInteractor: StockPriceInteractor,
    protected val mDispatchesProvider: DispatchersProvider
) : ViewModel() {

    protected val mStocksLoadState = MutableStateFlow<StockLoadState>(LoadState.None())
    val stocksLoadState: StateFlow<StockLoadState>
        get() = mStocksLoadState.asStateFlow()

    val stocksAdapter: BaseRecyclerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        BaseRecyclerAdapter(
            createStocksAdapter(
                onStockClick = this::onStockClick,
                onFavouriteIconClick = this::onFavouriteIconClick,
                onBindCallback = this::onBind
            )
        ).apply { setHasStableIds(true) }
    }

    val companiesStockPriceUpdates: SharedFlow<CompanyWithStockPrice> = mCompaniesInteractor
        .companyWithStockPriceChanges
        .onEach { onStockPriceUpdate(it) }
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(SHARING_STOP_TIMEOUT))

    val favouriteCompaniesUpdates: SharedFlow<Company> = mCompaniesInteractor
        .observeFavouriteCompaniesUpdates()
        .onEach { onFavouriteCompanyUpdate(it) }
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(SHARING_STOP_TIMEOUT))

    val actualStockPrice: SharedFlow<StockPriceState> = mStockPriceInteractor
        .observeActualStockPriceResponses()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(SHARING_STOP_TIMEOUT))

    fun loadStocks(onlyFavourites: Boolean = false) {
        viewModelScope.launch(mDispatchesProvider.IO) {

            mStocksLoadState.value = LoadState.Loading()

            val companies = if (onlyFavourites) {
                mCompaniesInteractor.getAllFavourites()
            } else {
                mCompaniesInteractor.getAll()
            }

            val viewData = companies.map(mStockMapper::map)

            mStocksLoadState.value = LoadState.Prepared(
                data = viewData
            )

            withContext(mDispatchesProvider.Main) {
                stocksAdapter.setData(viewData)
            }
        }
    }

    open suspend fun onFavouriteStockViewDataUpdate(stockViewData: StockViewData) {
        val targetPosition = stocksAdapter.getPosition {
            stockViewData.getUniqueId() == it.getUniqueId()
        }
        withContext(mDispatchesProvider.Main) {
            stocksAdapter.update(
                stockViewData,
                targetPosition,
                mutableListOf(PAYLOAD_FAVOURITE_UPDATED)
            )
        }
    }

    private fun onStockClick(stockViewData: StockViewData) {
        // TODO abstract
    }

    private fun onFavouriteIconClick(stockViewData: StockViewData) {
        viewModelScope.launch(mDispatchesProvider.IO) {
            val company = mStockMapper.map(stockViewData)

            if (company.isFavourite) {
                mCompaniesInteractor.addCompanyToFavourites(company)
            } else {
                mCompaniesInteractor.removeCompanyFromFavourites(company)
            }
        }
    }

    private fun onBind(stockViewData: StockViewData, position: Int) {
        viewModelScope.launch(mDispatchesProvider.IO) {
            mStockPriceInteractor.addRequestToGetStockPrice(
                stockViewData.id,
                stockViewData.ticker,
                position
            )
        }
    }

    private fun onStockPriceUpdate(companyWithStockPrice: CompanyWithStockPrice) {
        viewModelScope.launch(mDispatchesProvider.IO) {
            mStocksLoadState.value.ifPrepared { preparedLoad ->
                preparedLoad.data.ifExist<StockViewData>(
                    selector = { companyWithStockPrice.company.id == it.id },
                    action = { companyPosition ->
                        val stockViewData = preparedLoad.data[companyPosition]
                        stockViewData.stockPrice = companyWithStockPrice.stockPrice

                        val targetPosition = stocksAdapter.getPosition {
                            stockViewData.getUniqueId() == it.getUniqueId()
                        }

                        stocksAdapter.update(
                            stockViewData,
                            targetPosition,
                            mutableListOf(PAYLOAD_PRICE_UPDATED)
                        )
                    }
                )
            }
        }
    }

    private suspend fun onFavouriteCompanyUpdate(company: Company) {
        mStocksLoadState.value.ifPrepared { preparedLoad ->

            preparedLoad.data.ifExist<Company>(
                selector = { it.id == company.id },
                action = { companyPosition ->
                    val companyToReplace = preparedLoad.data[companyPosition]
                    companyToReplace.isFavourite = company.isFavourite

                    onFavouriteStockViewDataUpdate(companyToReplace)
                }
            )
        }
    }
}