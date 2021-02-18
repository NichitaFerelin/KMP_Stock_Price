package com.ferelin.remote.network.stockCandles

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class StockCandlesManager(
    private val mOnResponse: (response: StockCandlesResponse) -> Unit
) : Callback<StockCandlesResponse.Success> {

    override fun onResponse(
        call: Call<StockCandlesResponse.Success>,
        response: Response<StockCandlesResponse.Success>
    ) {
        val responseBody = response.body()
        if (response.isSuccessful && responseBody != null && responseBody.responseStatus == "ok") {
            mOnResponse(responseBody)
        } else mOnResponse(StockCandlesResponse.Fail(Throwable(response.message())))
    }

    override fun onFailure(call: Call<StockCandlesResponse.Success>, t: Throwable) {
        mOnResponse(StockCandlesResponse.Fail(t))
    }

    companion object {
        val API = StockCandlesApi::class.java
    }
}