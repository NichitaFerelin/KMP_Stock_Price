package com.ferelin.remote.network.stockSymbol

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StockSymbolManager(
    private val mOnResponse: (response: List<StockSymbolResponse>) -> Unit
) : Callback<List<StockSymbolResponse.Success>> {
    override fun onResponse(
        call: Call<List<StockSymbolResponse.Success>>,
        response: Response<List<StockSymbolResponse.Success>>
    ) {
        val responseBody = response.body()
        if (response.isSuccessful && responseBody != null) {
            mOnResponse(responseBody)
        } else mOnResponse(listOf(StockSymbolResponse.Fail(Throwable(response.message()))))
    }

    override fun onFailure(call: Call<List<StockSymbolResponse.Success>>, t: Throwable) {
        mOnResponse(listOf(StockSymbolResponse.Fail(t)))
    }

    companion object {
        val API = StockSymbolApi::class.java
    }
}