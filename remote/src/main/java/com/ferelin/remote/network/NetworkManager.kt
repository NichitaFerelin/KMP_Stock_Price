package com.ferelin.remote.network

import com.ferelin.remote.base.BaseManager
import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.network.companyNews.CompanyNewsApi
import com.ferelin.remote.network.companyNews.CompanyNewsResponse
import com.ferelin.remote.network.companyProfile.CompanyProfileApi
import com.ferelin.remote.network.companyProfile.CompanyProfileResponse
import com.ferelin.remote.network.companyQuote.CompanyQuoteApi
import com.ferelin.remote.network.companyQuote.CompanyQuoteResponse
import com.ferelin.remote.network.stockCandles.StockCandlesApi
import com.ferelin.remote.network.stockCandles.StockCandlesResponse
import com.ferelin.remote.network.stockSymbols.StockSymbolApi
import com.ferelin.remote.network.stockSymbols.StockSymbolResponse
import com.ferelin.remote.network.throttleManager.ThrottleManager
import com.ferelin.remote.network.throttleManager.ThrottleManagerHelper
import com.ferelin.remote.utilits.Api
import com.ferelin.remote.utilits.RetrofitDelegate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flowOn
import retrofit2.Retrofit

class NetworkManager : NetworkManagerHelper {

    private val mRetrofit: Retrofit by RetrofitDelegate(Api.FINNHUB_BASE_URL)

    private val mCompanyProfileService = mRetrofit.create(CompanyProfileApi::class.java)
    private val mCompanyNewsService = mRetrofit.create(CompanyNewsApi::class.java)
    private val mCompanyQuoteService = mRetrofit.create(CompanyQuoteApi::class.java)
    private val mStockCandlesService = mRetrofit.create(StockCandlesApi::class.java)
    private val mStockSymbolsService = mRetrofit.create(StockSymbolApi::class.java)

    private val mThrottleManager: ThrottleManagerHelper = ThrottleManager()

    override fun loadStockSymbols(): Flow<BaseResponse> = callbackFlow {
        mStockSymbolsService
            .getStockSymbolList(Api.FINNHUB_TOKEN)
            .enqueue(BaseManager<StockSymbolResponse> {
                offer(it)
            })
        awaitClose()
    }.flowOn(Dispatchers.IO)

    override fun loadCompanyProfile(symbol: String): Flow<BaseResponse> = callbackFlow {
        mCompanyProfileService
            .getCompanyProfile(symbol, Api.FINNHUB_TOKEN)
            .enqueue(BaseManager<CompanyProfileResponse> {
                offer(it)
            })
        awaitClose()
    }.flowOn(Dispatchers.IO)

    override fun loadStockCandles(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): Flow<BaseResponse> = callbackFlow {
        mStockCandlesService
            .getStockCandles(symbol, Api.FINNHUB_TOKEN, from, to, resolution)
            .enqueue(BaseManager<StockCandlesResponse> {
                offer(it)
            })
        awaitClose()
    }.flowOn(Dispatchers.IO)

    override fun loadCompanyNews(symbol: String, from: String, to: String): Flow<BaseResponse> =
        callbackFlow {
            mCompanyNewsService
                .getCompanyNews(symbol, Api.FINNHUB_TOKEN, from, to)
                .enqueue(BaseManager<CompanyNewsResponse> {
                    offer(it)
                })
            awaitClose()
        }.flowOn(Dispatchers.IO)

    override fun loadCompanyQuote(symbol: String, position: Int): Flow<BaseResponse> =
        callbackFlow {
            mThrottleManager.addMessage(symbol, Api.COMPANY_QUOTE, position)
            mThrottleManager.setUpApi(Api.COMPANY_QUOTE) { symbolToRequest ->
                mCompanyQuoteService
                    .getCompanyQuote(symbolToRequest, Api.FINNHUB_TOKEN)
                    .enqueue(BaseManager<CompanyQuoteResponse> {
                        it.message = symbolToRequest
                        offer(it)
                    })
            }
            awaitClose { mThrottleManager.invalidate() }
        }.flowOn(Dispatchers.IO)
}