package com.ferelin.remote.network

import com.ferelin.remote.base.BaseResponse
import kotlinx.coroutines.flow.Flow


interface NetworkManagerHelper {

    fun loadStockSymbols(): Flow<BaseResponse>

    fun loadCompanyProfile(symbol: String): Flow<BaseResponse>

    fun loadStockCandle(
        symbol: String,
        position: Int,
        from: Long,
        to: Long,
        resolution: String
    ): Flow<BaseResponse>
}