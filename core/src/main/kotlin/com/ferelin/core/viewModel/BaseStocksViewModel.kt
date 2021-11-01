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
import com.ferelin.core.adapter.stocks.StockViewHolder
import com.ferelin.core.adapter.stocks.createStocksAdapter
import com.ferelin.core.mapper.CompanyWithStockPriceMapper
import com.ferelin.core.mapper.StockPriceMapper
import com.ferelin.core.params.AboutParams
import com.ferelin.core.utils.SHARING_STOP_TIMEOUT
import com.ferelin.core.utils.StockStyleProvider
import com.ferelin.core.viewData.StockViewData
import com.ferelin.domain.entities.CompanyWithStockPrice
import com.ferelin.domain.entities.StockPrice
import com.ferelin.domain.interactors.StockPriceInteractor
import com.ferelin.domain.interactors.companies.CompaniesInteractor
import com.ferelin.navigation.Router
import com.ferelin.shared.LoadState
import com.ferelin.shared.NULL_INDEX
import com.ferelin.shared.ifPrepared
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class StocksMode {
    ALL,
    ONLY_FAVOURITES
}

abstract class BaseStocksViewModel(
    protected val companyWithStockPriceMapper: CompanyWithStockPriceMapper,
    protected val router: Router,
    private val companiesInteractor: CompaniesInteractor,
    private val stockPriceInteractor: StockPriceInteractor,
    private val stockStyleProvider: StockStyleProvider,
    private val stockPriceMapper: StockPriceMapper,
    private val stocksMode: StocksMode
) : ViewModel() {

    protected val stockLoadState =
        MutableStateFlow<LoadState<List<StockViewData>>>(LoadState.None())

    val stocksAdapter: BaseRecyclerAdapter by lazy(LazyThreadSafetyMode.NONE) {
        BaseRecyclerAdapter(
            createStocksAdapter(
                onStockClick = this::onStockClick,
                onFavouriteIconClick = this::onFavouriteIconClick,
                onBindCallback = this::onBind
            )
        ).apply { setHasStableIds(true) }
    }

    val companiesStockPriceUpdates: SharedFlow<CompanyWithStockPrice> = companiesInteractor
        .companyWithStockPriceUpdates
        .onEach { onStockPriceUpdate(it) }
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(SHARING_STOP_TIMEOUT), 1)

    val favouriteCompaniesUpdates: SharedFlow<CompanyWithStockPrice> = companiesInteractor
        .favouriteCompaniesUpdates
        .onEach { onFavouriteCompanyUpdate(it) }
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(SHARING_STOP_TIMEOUT))

    val actualStockPrice: SharedFlow<LoadState<StockPrice>> = stockPriceInteractor
        .observeActualStockPriceResponses()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(SHARING_STOP_TIMEOUT), 1)

    init {
        prepareStocksData()
    }

    fun onHolderUntouched(stockViewHolder: StockViewHolder) {
        viewModelScope.launch {
            val viewData = stocksAdapter.getByPosition(stockViewHolder.layoutPosition)
            if (viewData is StockViewData) {
                onFavouriteIconClick(viewData)
            }
        }
    }

    protected open suspend fun onFavouriteCompanyUpdate(
        companyWithStockPrice: CompanyWithStockPrice
    ) {
        stockLoadState.value.ifPrepared { preparedLoad ->

            val targetIndex = preparedLoad
                .data
                .indexOfFirst { companyWithStockPrice.company.id == it.id }

            if (targetIndex != NULL_INDEX) {
                val targetCompany = preparedLoad.data[targetIndex]
                targetCompany.isFavourite = companyWithStockPrice.company.isFavourite

                stockStyleProvider.updateFavourite(targetCompany)

                updateItemAtAdapter(targetCompany, PAYLOAD_FAVOURITE_UPDATED)
            }
        }
    }

    private fun prepareStocksData() {
        viewModelScope.launch {
            stockLoadState.value = LoadState.Loading()

            val companies = if (stocksMode == StocksMode.ALL) {
                companiesInteractor.getAll()
            } else {
                companiesInteractor.getAllFavourites()
            }

            val viewData = companies.map(companyWithStockPriceMapper::map)

            stockLoadState.value = LoadState.Prepared(viewData)

            withContext(Dispatchers.Main) {
                stocksAdapter.setData(viewData)
            }
        }
    }

    private fun onStockClick(stockViewData: StockViewData) {
        viewModelScope.launch {
            router.fromDefaultStocksToAbout(
                params = AboutParams(
                    companyId = stockViewData.id,
                    companyTicker = stockViewData.ticker,
                    companyName = stockViewData.name,
                    logoUrl = stockViewData.logoUrl,
                    isFavourite = stockViewData.isFavourite,
                    stockPrice = stockViewData.stockPriceViewData?.price ?: "",
                    stockProfit = stockViewData.stockPriceViewData?.profit ?: ""
                )
            )
        }
    }

    private fun onFavouriteIconClick(stockViewData: StockViewData) {
        viewModelScope.launch {
            val company = companyWithStockPriceMapper.map(stockViewData)

            if (company.isFavourite) {
                companiesInteractor.eraseCompanyFromFavourites(company)
            } else {
                companiesInteractor.addCompanyToFavourites(company)
            }
        }
    }

    private fun onBind(stockViewData: StockViewData, position: Int) {
        viewModelScope.launch {
            stockPriceInteractor.addRequestToGetStockPrice(
                stockViewData.id,
                stockViewData.ticker,
                position
            )
        }
    }

    private fun onStockPriceUpdate(companyWithStockPrice: CompanyWithStockPrice) {
        viewModelScope.launch {
            stockLoadState.value.ifPrepared { preparedLoad ->

                val targetIndex = preparedLoad
                    .data
                    .indexOfFirst { companyWithStockPrice.company.id == it.id }

                if (targetIndex != NULL_INDEX) {
                    val stockViewData = preparedLoad.data[targetIndex]

                    companyWithStockPrice.stockPrice?.let {
                        stockViewData.stockPriceViewData = stockPriceMapper.map(it)
                    }

                    stockStyleProvider.updateProfit(stockViewData)
                    updateItemAtAdapter(stockViewData, PAYLOAD_PRICE_UPDATED)
                }
            }
        }
    }

    private suspend fun updateItemAtAdapter(
        stockViewData: StockViewData,
        payloads: Any? = null
    ) {
        val targetPosition = stocksAdapter.getPositionOf {
            stockViewData.getUniqueId() == it.getUniqueId()
        }

        if (targetPosition != NULL_INDEX) {
            withContext(Dispatchers.Main) {
                stocksAdapter.update(stockViewData, targetPosition, payloads)
            }
        }
    }
}