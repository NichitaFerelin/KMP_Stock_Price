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

package com.ferelin.remote.api

import com.ferelin.remote.COMPANY_QUOTE
import com.ferelin.remote.RESPONSE_UNDEFINED
import com.ferelin.remote.api.companyNews.CompanyNewsApi
import com.ferelin.remote.api.companyNews.CompanyNewsResponse
import com.ferelin.remote.api.companyProfile.CompanyProfileApi
import com.ferelin.remote.api.companyProfile.CompanyProfileResponse
import com.ferelin.remote.api.stockHistory.StockHistoryApi
import com.ferelin.remote.api.stockHistory.StockHistoryResponse
import com.ferelin.remote.api.stockPrice.StockPriceApi
import com.ferelin.remote.api.stockPrice.StockPriceResponse
import com.ferelin.remote.api.stockSymbols.StockSymbolApi
import com.ferelin.remote.api.stockSymbols.StockSymbolResponse
import com.ferelin.remote.api.throttleManager.ThrottleManager
import com.ferelin.remote.base.BaseManager
import com.ferelin.remote.base.BaseResponse
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Retrofit
import java.net.SocketTimeoutException
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
open class ApiManagerImpl @Inject constructor(
    private val mThrottleManager: ThrottleManager,
    @Named("FinnhubToken") private val mToken: String,
    retrofit: Retrofit
) : ApiManager {

    private val mCompanyProfileApi by lazy { retrofit.create(CompanyProfileApi::class.java) }
    private val mCompanyNewsApi by lazy { retrofit.create(CompanyNewsApi::class.java) }
    private val mStockPriceApi by lazy { retrofit.create(StockPriceApi::class.java) }
    private val mStockHistoryApi by lazy { retrofit.create(StockHistoryApi::class.java) }
    private val mStockSymbolsApi by lazy { retrofit.create(StockSymbolApi::class.java) }

    override fun loadStockSymbols(): BaseResponse<StockSymbolResponse> {
        val retrofitResponse = mStockSymbolsApi
            .getStockSymbolList(mToken)
            .execute()
        return BaseResponse.createResponse(
            retrofitResponse.body(),
            retrofitResponse.code()
        )
    }

    override fun loadCompanyProfile(symbol: String): BaseResponse<CompanyProfileResponse> {
        return try {
            val retrofitResponse = mCompanyProfileApi
                .getCompanyProfile(symbol, mToken)
                .execute()
            BaseResponse.createResponse(
                retrofitResponse.body(),
                retrofitResponse.code()
            )
        } catch (exception: SocketTimeoutException) {
            BaseResponse.createResponse(
                null,
                RESPONSE_UNDEFINED
            )
        }
    }

    override fun loadStockHistory(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): BaseResponse<StockHistoryResponse> {
        return try {
            val retrofitResponse = mStockHistoryApi
                .getStockCandles(symbol, mToken, from, to, resolution)
                .execute()
            BaseResponse.createResponse(
                responseBody = retrofitResponse.body(),
                responseCode = retrofitResponse.code()
            )
        } catch (exception: SocketTimeoutException) {
            BaseResponse.createResponse(
                null,
                RESPONSE_UNDEFINED
            )
        }
    }

    override fun loadCompanyNews(
        symbol: String,
        from: String,
        to: String
    ): BaseResponse<List<CompanyNewsResponse>> {
        return try {
            val retrofitResponse = mCompanyNewsApi
                .getCompanyNews(symbol, mToken, from, to)
                .execute()
            BaseResponse.createResponse(
                responseBody = retrofitResponse.body(),
                responseCode = retrofitResponse.code()
            )
        } catch (exception: SocketTimeoutException) {
            BaseResponse.createResponse(
                null,
                RESPONSE_UNDEFINED
            )
        }
    }

    override fun sendRequestToLoadPrice(
        symbol: String,
        position: Int,
        isImportant: Boolean
    ) {
        // Add message(request) to throttle manager
        mThrottleManager.addRequestToOrder(
            companyOwnerSymbol = symbol,
            apiTag = COMPANY_QUOTE,
            messageNumber = position,
            eraseIfNotActual = !isImportant,
            ignoreDuplicates = isImportant
        )
    }

    override fun getStockPriceResponseState(): Flow<BaseResponse<StockPriceResponse>> =
        callbackFlow {
            mThrottleManager.setUpApi(COMPANY_QUOTE) { symbolToRequest ->
                mStockPriceApi
                    .getCompanyQuote(symbolToRequest, mToken)
                    .enqueue(BaseManager<StockPriceResponse> {
                        it.additionalMessage = symbolToRequest
                        trySend(it)
                    })
            }
            awaitClose { mThrottleManager.invalidate() }
        }
}