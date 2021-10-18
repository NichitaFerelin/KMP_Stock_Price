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
import com.ferelin.core.mapper.StockMapper
import com.ferelin.core.params.AboutParams
import com.ferelin.core.utils.SHARING_STOP_TIMEOUT
import com.ferelin.core.utils.StockStyleProvider
import com.ferelin.core.viewData.StockViewData
import com.ferelin.domain.entities.CompanyWithStockPrice
import com.ferelin.domain.entities.StockPrice
import com.ferelin.domain.interactors.StockPriceInteractor
import com.ferelin.domain.interactors.companies.CompaniesInteractor
import com.ferelin.navigation.Router
import com.ferelin.shared.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

enum class StocksMode {
    ALL,
    ONLY_FAVOURITES
}

abstract class BaseStocksViewModel(
    protected val stockMapper: StockMapper,
    protected val dispatchesProvider: DispatchersProvider,
    private val companiesInteractor: CompaniesInteractor,
    private val stockPriceInteractor: StockPriceInteractor,
    private val stockStyleProvider: StockStyleProvider,
    private val router: Router
) : ViewModel() {

    abstract val stocksMode: StocksMode

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
        .companyWithStockPriceChanges
        .onEach { onStockPriceUpdate(it) }
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(SHARING_STOP_TIMEOUT))

    val favouriteCompaniesUpdates: SharedFlow<CompanyWithStockPrice> = companiesInteractor
        .favouriteCompaniesUpdates
        .onEach { onFavouriteCompanyUpdate(it) }
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(SHARING_STOP_TIMEOUT))

    val actualStockPrice: SharedFlow<LoadState<StockPrice>> = stockPriceInteractor
        .observeActualStockPriceResponses()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(SHARING_STOP_TIMEOUT))

    init {
        loadStocks()
    }

    fun onHolderUntouched(stockViewHolder: StockViewHolder) {
        viewModelScope.launch(dispatchesProvider.IO) {
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
            val uniqueId = companyWithStockPrice.company.id.toLong()

            preparedLoad.data.ifExist<StockViewData>(
                selector = { uniqueId == it.getUniqueId() },
                action = { companyPosition ->
                    val companyToReplace = preparedLoad.data[companyPosition]
                    companyToReplace.isFavourite = companyWithStockPrice.company.isFavourite
                    stockStyleProvider.updateFavourite(companyToReplace)

                    updateItemAtAdapter(companyToReplace, PAYLOAD_FAVOURITE_UPDATED)
                }
            )
        }
    }

    private fun loadStocks() {
        viewModelScope.launch {
            stockLoadState.value = LoadState.Loading()

            val companies = if (stocksMode == StocksMode.ALL) {
                companiesInteractor.getAll()
            } else {
                companiesInteractor.getAllFavourites()
            }

            val viewData = companies.map(stockMapper::map)

            stockLoadState.value = LoadState.Prepared(
                data = viewData
            )

            withContext(dispatchesProvider.Main) {
                stocksAdapter.setData(viewData)
            }
        }
    }

    private fun onStockClick(stockViewData: StockViewData) {
        viewModelScope.launch {
            val (price, profit) = stockViewData
                .stockPrice
                ?.let { arrayOf(it.currentPrice, it.profit) }
                ?: arrayOf("", "")

            router.fromDefaultStocksToAbout(
                params = AboutParams(
                    companyId = stockViewData.id,
                    companyTicker = stockViewData.ticker,
                    companyName = stockViewData.name,
                    logoUrl = stockViewData.logoUrl,
                    isFavourite = stockViewData.isFavourite,
                    stockPrice = price,
                    stockProfit = profit
                )
            )
        }
    }

    private fun onFavouriteIconClick(stockViewData: StockViewData) {
        viewModelScope.launch(dispatchesProvider.IO) {
            val company = stockMapper.map(stockViewData)

            if (company.isFavourite) {
                companiesInteractor.eraseCompanyFromFavourites(company)
            } else {
                companiesInteractor.addCompanyToFavourites(company)
            }
        }
    }

    private fun onBind(stockViewData: StockViewData, position: Int) {
        viewModelScope.launch(dispatchesProvider.IO) {
            stockPriceInteractor.addRequestToGetStockPrice(
                stockViewData.id,
                stockViewData.ticker,
                position
            )
        }
    }

    private fun onStockPriceUpdate(companyWithStockPrice: CompanyWithStockPrice) {
        viewModelScope.launch(dispatchesProvider.IO) {
            stockLoadState.value.ifPrepared { preparedLoad ->

                preparedLoad.data.ifExist<StockViewData>(
                    selector = { companyWithStockPrice.company.id == it.id },
                    action = { companyPosition ->
                        val stockViewData = preparedLoad.data[companyPosition]
                        stockViewData.stockPrice = companyWithStockPrice.stockPrice
                        stockStyleProvider.updateProfit(stockViewData)

                        updateItemAtAdapter(stockViewData, PAYLOAD_PRICE_UPDATED)
                    }
                )
            }
        }
    }

    private suspend fun updateItemAtAdapter(
        stockViewData: StockViewData,
        payloads: Any? = null
    ) {
        val targetPosition = stocksAdapter.getPosition {
            stockViewData.getUniqueId() == it.getUniqueId()
        }

        if (targetPosition != NULL_INDEX) {
            withContext(dispatchesProvider.Main) {
                stocksAdapter.update(stockViewData, targetPosition, payloads)
            }
        }
    }
}