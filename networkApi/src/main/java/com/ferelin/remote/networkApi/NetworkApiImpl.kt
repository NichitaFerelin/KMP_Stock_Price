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

package com.ferelin.remote.networkApi

import com.ferelin.remote.networkApi.entities.*
import com.ferelin.remote.networkApi.requestsLimiter.RequestsLimiter
import com.ferelin.remote.utils.BaseResponse
import com.ferelin.remote.utils.COMPANY_ACTUAL_PRICE
import com.ferelin.remote.utils.withExceptionHandle
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
open class NetworkApiImpl @Inject constructor(
    @Named("FinnhubToken") private val mToken: String,
    private val mRequestsLimiter: RequestsLimiter,
    private val mCompanyNewsApi: CompanyNewsApi,
    private val mActualStockPriceApi: ActualStockPriceApi,
    private val mPriceChangesHistoryApi: PriceChangesHistoryApi
) : NetworkApi {

    override fun loadPriceChangesHistory(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): BaseResponse<StockPriceHistoryResponse> {
        return withExceptionHandle(
            requestOwner = symbol,
            request = {
                mPriceChangesHistoryApi
                    .getPriceChangesHistory(symbol, mToken, from, to, resolution)
                    .execute()
            }
        )
    }

    override fun loadCompanyNews(
        symbol: String,
        from: String,
        to: String
    ): BaseResponse<List<CompanyNewsResponse>> {
        return withExceptionHandle(
            requestOwner = symbol,
            request = {
                mCompanyNewsApi
                    .getCompanyNews(symbol, mToken, from, to)
                    .execute()
            }
        )
    }

    override fun loadActualStockPriceWithLimiter(
        symbol: String,
        keyPosition: Int,
        isImportant: Boolean
    ) {
        mRequestsLimiter.addRequestToOrder(
            companyOwnerSymbol = symbol,
            apiTag = COMPANY_ACTUAL_PRICE,
            keyPosition = keyPosition,
            eraseIfNotActual = !isImportant,
            ignoreDuplicates = isImportant
        )
    }

    override fun observeActualStockPriceResponses() =
        callbackFlow<BaseResponse<ActualStockPriceResponse>> {
            mRequestsLimiter.setUpApi(COMPANY_ACTUAL_PRICE) { symbolToRequest ->
                val response = withExceptionHandle(
                    requestOwner = symbolToRequest,
                    request = {
                        mActualStockPriceApi
                            .getActualStockPrice(symbolToRequest, mToken)
                            .execute()
                    }
                )
                trySend(response)
            }
            awaitClose { mRequestsLimiter.invalidate() }
        }
}