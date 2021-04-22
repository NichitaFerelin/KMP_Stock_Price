package com.ferelin.remote.base

import com.ferelin.remote.utils.Api
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/*
* Common onResponse() logic for all network responses.
* */
class BaseManager<T>(
    private val mOnResponse: (response: BaseResponse<T>) -> Unit
) : Callback<T> {

    override fun onResponse(call: Call<T>, response: Response<T>) {
        val responseBody = response.body()
        val responseCode = response.code()

        when {
            responseCode == 429 -> mOnResponse(BaseResponse(Api.RESPONSE_LIMIT))
            responseBody == null -> mOnResponse(BaseResponse(Api.RESPONSE_NO_DATA))
            responseCode == 200 -> {
                mOnResponse(
                    BaseResponse(
                        responseCode = Api.RESPONSE_OK,
                        additionalMessage = null,
                        responseData = responseBody
                    )
                )
            }
        }
    }

    override fun onFailure(call: Call<T>, t: Throwable) {
        mOnResponse(BaseResponse(Api.RESPONSE_UNDEFINED))
    }
}