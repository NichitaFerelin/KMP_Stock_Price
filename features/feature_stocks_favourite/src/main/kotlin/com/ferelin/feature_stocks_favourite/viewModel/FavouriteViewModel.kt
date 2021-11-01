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

package com.ferelin.feature_stocks_favourite.viewModel

import com.ferelin.core.mapper.CompanyWithStockPriceMapper
import com.ferelin.core.mapper.StockPriceMapper
import com.ferelin.core.utils.StockStyleProvider
import com.ferelin.core.viewModel.BaseStocksViewModel
import com.ferelin.core.viewModel.StocksMode
import com.ferelin.domain.entities.CompanyWithStockPrice
import com.ferelin.domain.interactors.StockPriceInteractor
import com.ferelin.domain.interactors.companies.CompaniesInteractor
import com.ferelin.navigation.Router
import com.ferelin.shared.LoadState
import com.ferelin.shared.ifPrepared
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FavouriteViewModel @Inject constructor(
    companyWithStockPriceMapper: CompanyWithStockPriceMapper,
    companiesInteractor: CompaniesInteractor,
    stockPriceInteractor: StockPriceInteractor,
    stockStyleProvider: StockStyleProvider,
    stockPriceMapper: StockPriceMapper,
    router: Router
) : BaseStocksViewModel(
    companyWithStockPriceMapper,
    router,
    companiesInteractor,
    stockPriceInteractor,
    stockStyleProvider,
    stockPriceMapper,
    StocksMode.ONLY_FAVOURITES
) {
    override suspend fun onFavouriteCompanyUpdate(companyWithStockPrice: CompanyWithStockPrice) {
        stockLoadState.value.ifPrepared { preparedLoad ->
            val favouriteCompanies = preparedLoad.data.toMutableList()
            val stockViewData = companyWithStockPriceMapper.map(companyWithStockPrice)

            if (companyWithStockPrice.company.isFavourite) {
                favouriteCompanies.add(0, stockViewData)

                withContext(Dispatchers.Main) {
                    stocksAdapter.add(0, stockViewData)
                }
            } else {
                favouriteCompanies.remove(stockViewData)

                val uniqueId = stockViewData.getUniqueId()
                val targetPosition = stocksAdapter.getPositionOf { uniqueId == it.getUniqueId() }
                withContext(Dispatchers.Main) {
                    stocksAdapter.removeAt(targetPosition)
                }
            }

            stockLoadState.value = LoadState.Prepared(favouriteCompanies)
        }
    }
}