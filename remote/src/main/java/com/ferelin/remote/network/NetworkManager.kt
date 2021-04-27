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
import com.ferelin.remote.utils.Api
import com.ferelin.remote.utils.RetrofitDelegate
import com.ferelin.remote.utils.offerSafe
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import retrofit2.Retrofit

open class NetworkManager : NetworkManagerHelper {

    private val mRetrofit: Retrofit by RetrofitDelegate(Api.FINNHUB_BASE_URL)

    private val mCompanyProfileService = mRetrofit.create(CompanyProfileApi::class.java)
    private val mCompanyNewsService = mRetrofit.create(CompanyNewsApi::class.java)
    private val mCompanyQuoteService = mRetrofit.create(CompanyQuoteApi::class.java)
    private val mStockCandlesService = mRetrofit.create(StockCandlesApi::class.java)
    private val mStockSymbolsService = mRetrofit.create(StockSymbolApi::class.java)

    private val mThrottleManager: ThrottleManagerHelper = ThrottleManager()

    override fun loadStockSymbols(): Flow<BaseResponse<StockSymbolResponse>> = callbackFlow {
        mStockSymbolsService
            .getStockSymbolList(Api.FINNHUB_TOKEN)
            .enqueue(BaseManager<StockSymbolResponse> {
                offer(it)
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