package com.ferelin.remote.network

import com.ferelin.remote.network.companyProfile.CompanyProfileManager
import com.ferelin.remote.network.companyProfile.CompanyProfileResponse
import com.ferelin.remote.network.stockCandles.StockCandlesManager
import com.ferelin.remote.network.stockCandles.StockCandlesResponse
import com.ferelin.remote.network.stockSymbol.StockSymbolManager
import com.ferelin.remote.network.stockSymbol.StockSymbolResponse
import com.ferelin.remote.utilits.Api
import com.ferelin.remote.utilits.RetrofitDelegate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.*
import retrofit2.Retrofit

class NetworkManager : NetworkManagerHelper {

    private val mRetrofit: Retrofit by RetrofitDelegate(Api.FINNHUB_BASE_URL)

    private val mCompanyProfileService = mRetrofit.create(CompanyProfileManager.API)
    private val mStockQuoteService = mRetrofit.create(StockCandlesManager.API)
    private val mStockSymbolsService = mRetrofit.create(StockSymbolManager.API)

    override fun loadStockSymbols(): Flow<List<StockSymbolResponse>> = callbackFlow {
        mStockSymbolsService
            .getStockSymbolList(Api.FINNHUB_TOKEN)
            .enqueue(StockSymbolManager {
                offer(it)
            })
        awaitClose()
    }.flowOn(Dispatchers.IO)

    override fun loadCompanyProfile(symbol: String): Flow<CompanyProfileResponse> = callbackFlow {
        mCompanyProfileService
            .getCompanyProfile(symbol, Api.FINNHUB_TOKEN)
            .enqueue(CompanyProfileManager {
                offer(it)
            })
        awaitClose()
    }.flowOn(Dispatchers.IO)

    override fun loadStockCandle(
        symbol: String,
        from: Double,
        to: Double
    ): Flow<StockCandlesResponse> = callbackFlow {
        mStockQuoteService
            .getStockCandle(symbol, Api.FINNHUB_TOKEN, from, to)
            .enqueue(StockCandlesManager {
                offer(it)
            })
        awaitClose()
    }.flowOn(Dispatchers.IO)

    override fun checkUpdates(previousLoadedSymbols: Collection<String>): Flow<List<StockSymbolResponse>> =
        flow {
            loadStockSymbols().first { updatedResponseSymbols ->
                val newSymbols = mutableListOf<StockSymbolResponse>()
                updatedResponseSymbols.forEach {
                    if (it is StockSymbolResponse.Success && !previousLoadedSymbols.contains(it.symbol)) {
                        newSymbols.add(it)
                    }
                }
                emit(newSymbols)
                true
            }
        }.flowOn(Dispatchers.IO)
}