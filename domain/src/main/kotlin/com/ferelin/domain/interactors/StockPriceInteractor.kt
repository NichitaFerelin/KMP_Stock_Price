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

package com.ferelin.domain.interactors

import com.ferelin.domain.entities.StockPrice
import com.ferelin.domain.repositories.StockPriceRepo
import com.ferelin.domain.sources.StockPriceSource
import com.ferelin.domain.utils.StockPriceListener
import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

sealed class StockPriceState {
    class Loaded(val stockPrice: StockPrice) : StockPriceState()
    object Error : StockPriceState()
}

@Singleton
class StockPriceInteractor @Inject constructor(
    private val mStockPriceRepo: StockPriceRepo,
    private val mStockPriceSource: StockPriceSource,
    private val mDispatchersProvider: DispatchersProvider,
    private val mPriceListeners: List<@JvmSuppressWildcards StockPriceListener>,
    @Named("ExternalScope") private val mExternalScope: CoroutineScope
) {
    fun observeActualStockPriceResponses(): Flow<StockPriceState> {
        return mStockPriceSource.observeActualStockPriceResponses()
            .onEach { cacheIfLoaded(it) }
    }

    suspend fun addRequestToGetStockPrice(
        companyId: Int,
        companyTicker: String,
        keyPosition: Int,
        isImportant: Boolean = false
    ) {
        mStockPriceSource.addRequestToGetStockPrice(
            companyId,
            companyTicker,
            keyPosition,
            isImportant
        )
    }

    private fun cacheIfLoaded(responseState: StockPriceState) {
        if (responseState is StockPriceState.Loaded) {
            mExternalScope.launch(mDispatchersProvider.IO) {
                mStockPriceRepo.cacheStockPrice(responseState.stockPrice)

                mPriceListeners.forEach { it.onStockPriceChanged(responseState.stockPrice) }
            }
        }
    }
}