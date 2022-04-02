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

package com.ferelin.domain.sources

import com.ferelin.domain.entities.StockPrice
import com.ferelin.shared.LoadState
import kotlinx.coroutines.flow.Flow

interface StockPriceSource {

    /**
     * Executes single request out of queue to load stock price
     * @param companyId is a company for which need to load stock price
     * @param companyTicker is a company ticker for which need to load stock price
     * */
    suspend fun loadStockPrice(companyId: Int, companyTicker: String): LoadState<StockPrice>

    /**
     * Allows to add request to load actual stock price for company
     * @param companyId is a company for which need to load stock price
     * @param companyTicker is a company ticker for which need to load stock price
     * @param keyPosition is a position which determines the "importance" of the request
     * @param isImportant if true guarantees the execution of the request
     * */
    suspend fun addRequestToGetStockPrice(
        companyId: Int,
        companyTicker: String,
        keyPosition: Int,
        isImportant: Boolean
    )

    fun observeActualStockPriceResponses(): Flow<LoadState<StockPrice>>
}