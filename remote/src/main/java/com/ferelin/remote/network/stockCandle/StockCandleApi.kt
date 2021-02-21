package com.ferelin.remote.network.stockCandle

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface StockCandleApi {

    @GET("stock/candle")
    fun getStockCandle(
        @Query("symbol") symbol: String,
        @Query("token") token: String,
        @Query("from") from: Long,
        @Query("to") to: Long,
        @Query("resolution") resolution: String,
    ): Call<StockCandleResponse>
}