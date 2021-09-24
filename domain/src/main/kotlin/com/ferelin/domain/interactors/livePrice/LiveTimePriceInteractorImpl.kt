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

package com.ferelin.domain.interactors.livePrice

import com.ferelin.domain.entities.LiveTimePrice
import com.ferelin.domain.internals.LiveTimePriceInternal
import com.ferelin.domain.repositories.StockPriceRepo
import com.ferelin.domain.sources.LivePriceSource
import com.ferelin.shared.CoroutineContextProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class LiveTimePriceInteractorImpl @Inject constructor(
    private val mLivePriceSource: LivePriceSource,
    private val mStockPriceRepo: StockPriceRepo,
    private val mCoroutineContextProvider: CoroutineContextProvider,
    @Named("ExternalScope") private val mExternalScope: CoroutineScope
) : LiveTimePriceInteractor, LiveTimePriceInternal {

    private val mChangedPricesContainer = HashMap<Int, LiveTimePrice>(30, 0.5f)

    override fun observeLiveTimeUpdates(): Flow<LiveTimePrice?> {
        return mLivePriceSource.observeLiveTimeUpdates()
            .filter { it != null }
            .onEach { mChangedPricesContainer[it!!.companyId] = it }
            .onCompletion { cacheContainerChanges() }
    }

    override suspend fun subscribeCompanyOnUpdates(companyTicker: String) {
        mLivePriceSource.subscribeCompanyOnUpdates(companyTicker)
    }

    override suspend fun unsubscribeCompanyFromUpdates(companyTicker: String) {
        mLivePriceSource.unsubscribeCompanyFromUpdates(companyTicker)
    }

    private fun cacheContainerChanges() {
        mExternalScope.launch(mCoroutineContextProvider.IO) {
            for ((companyId, liveTimePrice) in mChangedPricesContainer) {
                mStockPriceRepo.updateStockPrice(
                    companyId = companyId,
                    price = liveTimePrice.price,
                    profit = liveTimePrice.profit
                )
            }

            mChangedPricesContainer.clear()
        }
    }
}