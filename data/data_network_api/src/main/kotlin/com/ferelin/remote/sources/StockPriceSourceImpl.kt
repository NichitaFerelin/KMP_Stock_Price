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

package com.ferelin.remote.sources

import com.ferelin.domain.interactors.StockPriceState
import com.ferelin.domain.sources.StockPriceSource
import com.ferelin.remote.entities.StockPriceApi
import com.ferelin.remote.mappers.StockPriceMapper
import com.ferelin.remote.utils.RequestsLimiter
import com.ferelin.remote.utils.withExceptionHandle
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named

class StockPriceSourceImpl @Inject constructor(
    private val mStockPriceApi: StockPriceApi,
    private val mStockPriceMapper: StockPriceMapper,
    private val mRequestsLimiter: RequestsLimiter,
    @Named("FinnhubToken") private val mApiToken: String
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
        mRequestsLimiter.addRequestToOrder(
            companyId,
            companyTicker,
            keyPosition,
            !isImportant,
            !isImportant
        )
    }

    override fun observeActualStockPriceResponses(): Flow<StockPriceState> =
        callbackFlow {
            Timber.d("observe actual stock price response")
            mRequestsLimiter.onExecuteRequest { companyId, tickerToExecute ->
                Timber.d("on execute request")
                withExceptionHandle(
                    request = {
                        mStockPriceApi
                            .getStockPrice(tickerToExecute, mApiToken)
                            .execute()
                    },
                    onSuccess = {
                        Timber.d("on success (response = $it)")
                        trySend(
                            StockPriceState.Loaded(mStockPriceMapper.map(it, companyId))
                        )
                    },
                    onFail = {
                        Timber.d("on fail (exception = $it)")
                        trySend(StockPriceState.Error)
                    }
                )
            }
            awaitClose { mRequestsLimiter.invalidate() }
        }
}