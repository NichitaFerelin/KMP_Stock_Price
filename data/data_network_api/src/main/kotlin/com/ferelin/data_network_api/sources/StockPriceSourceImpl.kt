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

package com.ferelin.data_network_api.sources

import com.ferelin.data_network_api.entities.StockPriceApi
import com.ferelin.data_network_api.mappers.StockPriceMapper
import com.ferelin.data_network_api.utils.RequestsLimiter
import com.ferelin.data_network_api.utils.withExceptionHandle
import com.ferelin.domain.entities.StockPrice
import com.ferelin.domain.sources.StockPriceSource
import com.ferelin.shared.LoadState
import com.ferelin.shared.NAMED_FINNHUB_TOKEN
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class StockPriceSourceImpl @Inject constructor(
    @Named(NAMED_FINNHUB_TOKEN) private val token: String,
    private val stockPriceApi: StockPriceApi,
    private val stockPriceMapper: StockPriceMapper,
    private val requestsLimiter: RequestsLimiter
) : StockPriceSource {

    override suspend fun addRequestToGetStockPrice(
        companyId: Int,
        companyTicker: String,
        keyPosition: Int,
        isImportant: Boolean
    ) {
        Timber.d(
            "add request to get stock price (companyTicker = $companyTicker," +
                    " keyPosition = $keyPosition, isImportant = $isImportant"
        )

        requestsLimiter.addRequestToOrder(
            companyId,
            companyTicker,
            keyPosition,
            eraseIfNotActual = !isImportant,
            ignoreDuplicates = !isImportant
        )
    }

    override fun observeActualStockPriceResponses(): Flow<LoadState<StockPrice>> =
        callbackFlow {
            Timber.d("observe actual stock price responses")

            requestsLimiter.onExecuteRequest { companyId, tickerToExecute ->
                withExceptionHandle(
                    request = {
                        stockPriceApi
                            .loadBy(tickerToExecute, token)
                            .execute()
                    },
                    onSuccess = {
                        trySend(
                            LoadState.Prepared(
                                data = stockPriceMapper.map(it, companyId)
                            )
                        )
                    },
                    onFail = {
                        trySend(LoadState.Error())
                        Unit
                    }
                )
            }
            awaitClose { requestsLimiter.invalidate() }
        }
}