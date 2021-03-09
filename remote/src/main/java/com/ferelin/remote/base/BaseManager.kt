package com.ferelin.remote.base

import android.util.Log
import com.ferelin.remote.utilits.Api
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BaseManager<T : BaseResponse>(
    private val mOnResponse: (response: BaseResponse) -> Unit
) : Callback<T> {

    override fun onResponse(call: Call<T>, response: Response<T>) {
        val responseBody = response.body()
        val responseCode = response.code()

        Log.d("Test1", "${responseBody.toString()}")

        when {
            responseCode == 429 -> mOnResponse(BaseResponse(Api.RESPONSE_LIMIT))
            responseBody == null -> mOnResponse(BaseResponse(Api.RESPONSE_NO_DATA))
            responseCode == 200 -> {
                responseBody.responseCode = Api.RESPONSE_OK
                mOnResponse(responseBody)
            }
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        mOnResponse(BaseResponse(Api.RESPONSE_UNDEFINED))
    }
}