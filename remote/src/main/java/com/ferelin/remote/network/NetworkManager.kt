package com.ferelin.remote.network

import com.ferelin.remote.base.BaseManager
import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.network.companyProfile.CompanyProfileApi
import com.ferelin.remote.network.companyProfile.CompanyProfileResponse
import com.ferelin.remote.network.stockCandle.StockCandleApi
import com.ferelin.remote.network.stockCandle.StockCandleResponse
import com.ferelin.remote.network.stockSymbols.StockSymbolApi
import com.ferelin.remote.network.stockSymbols.StockSymbolResponse
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
    private val mStockQuoteService = mRetrofit.create(StockCandleApi::class.java)
    private val mStockSymbolsService = mRetrofit.create(StockSymbolApi::class.java)

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

    override fun loadStockCandle(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): Flow<BaseResponse> = callbackFlow {
        mStockQuoteService
            .getStockCandle(symbol, Api.FINNHUB_TOKEN, from, to, resolution)
            .enqueue(BaseManager<StockCandleResponse> {
                offer(it)
            })
        awaitClose()
    }.flowOn(Dispatchers.IO)
}