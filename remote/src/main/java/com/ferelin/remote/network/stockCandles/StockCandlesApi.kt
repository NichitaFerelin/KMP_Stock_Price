package com.ferelin.remote.network.stockCandles

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface StockCandlesApi {

    @GET("stock/candle")
    fun getStockCandles(
        @Query("symbol") symbol: String,
        @Query("token") token: String,
        @Query("from") from: Long,
        @Query("to") to: Long,
        @Query("resolution") resolution: String,
    ): Call<StockCandlesResponse>
}