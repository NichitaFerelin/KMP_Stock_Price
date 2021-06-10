package com.ferelin.remote.api

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

import com.ferelin.remote.api.companyNews.CompanyNewsApi
import com.ferelin.remote.api.companyNews.CompanyNewsResponse
import com.ferelin.remote.api.companyProfile.CompanyProfileApi
import com.ferelin.remote.api.companyProfile.CompanyProfileResponse
import com.ferelin.remote.api.companyQuote.CompanyQuoteApi
import com.ferelin.remote.api.companyQuote.CompanyQuoteResponse
import com.ferelin.remote.api.stockCandles.StockCandlesApi
import com.ferelin.remote.api.stockCandles.StockCandlesResponse
import com.ferelin.remote.api.stockSymbols.StockSymbolApi
import com.ferelin.remote.api.stockSymbols.StockSymbolResponse
import com.ferelin.remote.api.throttleManager.ThrottleManager
import com.ferelin.remote.base.BaseManager
import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.utils.Api
import com.ferelin.remote.utils.offerSafe
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class ApiManagerImpl @Inject constructor(
    private val mThrottleManager: ThrottleManager,
    retrofit: Retrofit
) : ApiManager {

    private val mCompanyProfileService = retrofit.create(CompanyProfileApi::class.java)
    private val mCompanyNewsService = retrofit.create(CompanyNewsApi::class.java)
    private val mCompanyQuoteService = retrofit.create(CompanyQuoteApi::class.java)
    private val mStockCandlesService = retrofit.create(StockCandlesApi::class.java)
    private val mStockSymbolsService = retrofit.create(StockSymbolApi::class.java)

    override fun loadStockSymbols(): Flow<BaseResponse<StockSymbolResponse>> = callbackFlow {
        mStockSymbolsService
            .getStockSymbolList(Api.FINNHUB_TOKEN)
            .enqueue(BaseManager<StockSymbolResponse> {
                offerSafe(it)
            })
        awaitClose()
    }

    override fun loadCompanyProfile(symbol: String): Flow<BaseResponse<CompanyProfileResponse>> =
        callbackFlow {
            mCompanyProfileService
                .getCompanyProfile(symbol, Api.FINNHUB_TOKEN)
                .enqueue(BaseManager<CompanyProfileResponse> {
                    offerSafe(it)
                })
            awaitClose()
        }

    override fun loadStockCandles(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): Flow<BaseResponse<StockCandlesResponse>> = callbackFlow {
        mStockCandlesService
            .getStockCandles(symbol, Api.FINNHUB_TOKEN, from, to, resolution)
            .enqueue(BaseManager<StockCandlesResponse> {
                offerSafe(it)
            })
        awaitClose()
    }

    override fun loadCompanyNews(
        symbol: String,
        from: String,
        to: String
    ): Flow<BaseResponse<List<CompanyNewsResponse>>> = callbackFlow {
        mCompanyNewsService
            .getCompanyNews(symbol, Api.FINNHUB_TOKEN, from, to)
            .enqueue(BaseManager<List<CompanyNewsResponse>> {
                offerSafe(it)
            })
        awaitClose()
    }

    override fun loadCompanyQuote(
        symbol: String,
        position: Int,
        isImportant: Boolean
    ): Flow<BaseResponse<CompanyQuoteResponse>> = callbackFlow {

        // Add message(request) to throttle manager
        mThrottleManager.addMessage(
            symbol = symbol,
            api = Api.COMPANY_QUOTE,
            position = position,
            eraseIfNotActual = !isImportant,
            ignoreDuplicate = isImportant
        )

        mThrottleManager.setUpApi(Api.COMPANY_QUOTE) { symbolToRequest ->
            mCompanyQuoteService
                .getCompanyQuote(symbolToRequest, Api.FINNHUB_TOKEN)
                .enqueue(BaseManager<CompanyQuoteResponse> {
                    it.additionalMessage = symbolToRequest
                    offerSafe(it)
                })
        }
        awaitClose { mThrottleManager.invalidate() }
    }
}