package com.ferelin.remote.network

import com.ferelin.remote.base.BaseResponse
import kotlinx.coroutines.flow.Flow


interface NetworkManagerHelper {

    fun loadStockSymbols(): Flow<BaseResponse>

    fun loadCompanyProfile(symbol: String): Flow<BaseResponse>

    fun loadStockCandles(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): Flow<BaseResponse>

    fun loadCompanyNews(symbol: String, from: String, to: String): Flow<BaseResponse>

    fun loadCompanyQuote(symbol: String, position: Int): Flow<BaseResponse>

    fun setThrottleManagerHistory(map: HashMap<String, Any?>)
}