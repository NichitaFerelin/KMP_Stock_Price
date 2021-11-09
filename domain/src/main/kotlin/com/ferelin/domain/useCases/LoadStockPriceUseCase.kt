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

package com.ferelin.domain.useCases

import com.ferelin.domain.entities.StockPrice
import com.ferelin.domain.repositories.StockPriceRepo
import com.ferelin.domain.sources.StockPriceSource
import com.ferelin.shared.LoadState
import com.ferelin.shared.ifPrepared
import javax.inject.Inject

class LoadStockPriceUseCase @Inject constructor(
    private val stockPriceSource: StockPriceSource,
    private val stockPriceRepo: StockPriceRepo
) {
    suspend fun loadStockPrice(companyId: Int, companyTicker: String): LoadState<StockPrice> {
        return stockPriceSource
            .loadStockPrice(companyId, companyTicker)
            .also { loadState ->
                loadState.ifPrepared {
                    stockPriceRepo.update(companyId, it.data.currentPrice)
                }
            }
    }
}