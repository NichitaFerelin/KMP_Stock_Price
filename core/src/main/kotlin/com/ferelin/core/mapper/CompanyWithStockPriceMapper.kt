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

package com.ferelin.core.mapper

import com.ferelin.core.utils.StockStyleProvider
import com.ferelin.core.viewData.StockViewData
import com.ferelin.domain.entities.Company
import com.ferelin.domain.entities.CompanyWithStockPrice
import javax.inject.Inject

class CompanyWithStockPriceMapper @Inject constructor(
    private val stockStyleProvider: StockStyleProvider,
    private val stockPriceMapper: StockPriceMapper
) {
    fun map(companyWithStockPrice: CompanyWithStockPrice): StockViewData {
        return StockViewData(
            id = companyWithStockPrice.company.id,
            name = companyWithStockPrice.company.name,
            ticker = companyWithStockPrice.company.ticker,
            logoUrl = companyWithStockPrice.company.logoUrl,
            style = stockStyleProvider.createStyle(
                companyWithStockPrice.company,
                companyWithStockPrice.stockPrice
            ),
            stockPriceViewData = companyWithStockPrice.stockPrice?.let { stockPriceMapper.map(it) },
            isFavourite = companyWithStockPrice.company.isFavourite,
            addedByIndex = companyWithStockPrice.company.addedByIndex
        )
    }

    fun map(stockViewData: StockViewData): Company {
        return Company(
            id = stockViewData.id,
            name = stockViewData.name,
            ticker = stockViewData.ticker,
            logoUrl = stockViewData.logoUrl,
            isFavourite = stockViewData.isFavourite,
            addedByIndex = stockViewData.addedByIndex
        )
    }
}