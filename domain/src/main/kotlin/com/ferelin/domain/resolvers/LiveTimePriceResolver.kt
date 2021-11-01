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
import com.ferelin.shared.NAMED_EXTERNAL_SCOPE
import com.ferelin.shared.NetworkListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

/**
 * [LiveTimePriceResolver] allows to interact with live-time-price web socket
 * */
@Singleton
class LiveTimePriceResolver @Inject constructor(
    private val livePriceSource: LivePriceSource,
    private val priceListeners: List<@JvmSuppressWildcards StockPriceListener>,
    @Named(NAMED_EXTERNAL_SCOPE) private val externalScope: CoroutineScope
) : NetworkListener {

    private var collectLivePriceJob: Job? = null

    override suspend fun onNetworkAvailable() {
        collectLivePriceJob?.cancel()
        collectLivePriceJob = externalScope.launch {
            // Creates new observer when network is available
            livePriceSource.observeLiveTimePriceUpdates()
                .filter { it != null }
                .collect { onLiveTimePrice(it!!) }
        }
    }

    override suspend fun onNetworkLost() {
        livePriceSource.cancelLiveTimeUpdates()
        collectLivePriceJob?.cancel()
    }

    private suspend fun onLiveTimePrice(liveTimePrice: LiveTimePrice) {
        // Notifies all listeners about new stock price
        priceListeners.forEach { it.onStockPriceChanged(liveTimePrice) }
    }
}