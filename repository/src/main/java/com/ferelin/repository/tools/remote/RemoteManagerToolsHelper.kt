package com.ferelin.repository.tools.remote

import com.ferelin.local.model.Company
import com.ferelin.repository.utilits.Response
import com.ferelin.repository.utilits.TimeMillis
import kotlinx.coroutines.flow.Flow

interface RemoteManagerToolsHelper {

    fun openConnection(dataToSubscribe: Collection<String>): Flow<Response<HashMap<String, Any>>>

    fun loadStockCandles(
        symbol: String,
        from: Long = TimeMillis.convertForRequest(System.currentTimeMillis() - TimeMillis.ONE_YEAR),
        to: Long = TimeMillis.convertForRequest(System.currentTimeMillis()),
        resolution: String = "D"
    ): Flow<Response<HashMap<String, Any>>>

    fun loadCompanyProfile(symbol: String): Flow<Response<Company>>

    fun loadStockSymbols(): Flow<Response<List<String>>>
}