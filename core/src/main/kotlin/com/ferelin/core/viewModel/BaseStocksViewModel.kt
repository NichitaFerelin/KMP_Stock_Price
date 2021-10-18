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

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.adapter.base.BaseRecyclerAdapter
import com.ferelin.core.adapter.stocks.PAYLOAD_FAVOURITE_UPDATED
import com.ferelin.core.adapter.stocks.PAYLOAD_PRICE_UPDATED
import com.ferelin.core.adapter.stocks.StockViewHolder
import com.ferelin.core.adapter.stocks.createStocksAdapter
import com.ferelin.core.mapper.StockMapper
import com.ferelin.core.params.AboutParams
import com.ferelin.core.resolvers.NetworkResolver
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

typealias StockLoadState = LoadState<List<StockViewData>>

enum class StocksMode {
    ALL,
    ONLY_FAVOURITES
}

abstract class BaseStocksViewModel(
    protected val mStockMapper: StockMapper,
    protected val mDispatchesProvider: DispatchersProvider,
    private val mCompaniesInteractor: CompaniesInteractor,
    private val mStockPriceInteractor: StockPriceInteractor,
    private val mStockStyleProvider: StockStyleProvider,
    private val mRouter: Router
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

    val favouriteCompaniesUpdates: SharedFlow<CompanyWithStockPrice> = mCompaniesInteractor
        .favouriteCompaniesUpdated
        .onEach { onFavouriteCompanyUpdate(it) }
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(SHARING_STOP_TIMEOUT))

    val actualStockPrice: SharedFlow<LoadState<StockPrice>> = mStockPriceInteractor
        .observeActualStockPriceResponses()
        .shareIn(viewModelScope, SharingStarted.WhileSubscribed(SHARING_STOP_TIMEOUT))

    init {
        Log.d("TEST", "INIT VIEW MODEL")
        // TODO
        /*viewModelScope.launch {
            launch { companiesStockPriceUpdates.collect() }
            launch { favouriteCompaniesUpdates.collect{
                Log.d("TEST", "COLLECT VIEW MODEL")
            } }
            launch { actualStockPrice.collect() }
        }*/
    }

    override fun onCleared() {
        super.onCleared()
        Log.d("TEST", "CLEAR VIEW MODEL")
    }

    fun loadStocks(stocksMode: StocksMode) {
        viewModelScope.launch(mDispatchesProvider.IO) {

            mStocksLoadState.value = LoadState.Loading()

            val companies = if (stocksMode == StocksMode.ALL) {
                mCompaniesInteractor.getAll()
            } else {
                mCompaniesInteractor.getAllFavourites()
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

    fun onHolderUntouched(stockViewHolder: StockViewHolder) {
        viewModelScope.launch(mDispatchesProvider.IO) {
            val viewData = stocksAdapter.getByPosition(stockViewHolder.layoutPosition)
            if (viewData is StockViewData) {
                onFavouriteIconClick(viewData)
            }
        }
    }

    protected open suspend fun onFavouriteCompanyUpdate(
        companyWithStockPrice: CompanyWithStockPrice
    ) {
        mStocksLoadState.value.ifPrepared { preparedLoad ->

            val uniqueId = companyWithStockPrice.company.id.toLong()

            preparedLoad.data.ifExist<StockViewData>(
                selector = { uniqueId == it.getUniqueId() },
                action = { companyPosition ->
                    val companyToReplace = preparedLoad.data[companyPosition]
                    companyToReplace.isFavourite = companyWithStockPrice.company.isFavourite
                    mStockStyleProvider.updateFavourite(companyToReplace)

                    updateItemAtAdapter(companyToReplace, PAYLOAD_FAVOURITE_UPDATED)
                }
            )
        }
    }

    private fun onStockClick(stockViewData: StockViewData) {
        viewModelScope.launch(mDispatchesProvider.IO) {
            val (price, profit) = stockViewData
                .stockPrice
                ?.let { arrayOf(it.currentPrice, it.profit) }
                ?: arrayOf("", "")

            mRouter.fromDefaultStocksToAbout(
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
        viewModelScope.launch(mDispatchesProvider.IO) {
            val company = mStockMapper.map(stockViewData)

            if (company.isFavourite) {
                mCompaniesInteractor.removeCompanyFromFavourites(company)
            } else {
                mCompaniesInteractor.addCompanyToFavourites(company)
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
                        mStockStyleProvider.updateProfit(stockViewData)

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
            withContext(mDispatchesProvider.Main) {
                stocksAdapter.update(stockViewData, targetPosition, payloads)
            }
        }
    }
}