package com.ferelin.remote.base

/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import com.ferelin.remote.utils.Api
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * [BaseManager] with common [onResponse] logic for all network responses.
 */
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