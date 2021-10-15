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

package com.ferelin.domain.resolvers

import com.ferelin.domain.entities.LiveTimePrice
import com.ferelin.domain.sources.LivePriceSource
import com.ferelin.domain.utils.StockPriceListener
import com.ferelin.shared.DispatchersProvider
import com.ferelin.shared.NetworkListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class LiveTimePriceResolver @Inject constructor(
    private val mLivePriceSource: LivePriceSource,
    private val mDispatchersProvider: DispatchersProvider,
    private val mPriceListeners: List<@JvmSuppressWildcards StockPriceListener>,
    @Named("ExternalScope") private val mExternalScope: CoroutineScope
) : NetworkListener {

    override suspend fun onNetworkAvailable() {
        mExternalScope.launch(mDispatchersProvider.IO) {
            mLivePriceSource.observeLiveTimePriceUpdates()
                .filter { it != null }
                .map { it!! }
                .collect { onLiveTimePrice(it) }
        }
    }

    override suspend fun onNetworkLost() {
        mLivePriceSource.cancelLiveTimeUpdates()
    }

    private suspend fun onLiveTimePrice(liveTimePrice: LiveTimePrice) {
        mPriceListeners.forEach { it.onStockPriceChanged(liveTimePrice) }
    }
}