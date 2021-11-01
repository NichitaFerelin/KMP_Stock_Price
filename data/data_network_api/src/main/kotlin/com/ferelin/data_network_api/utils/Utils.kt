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

package com.ferelin.data_network_api.utils

import retrofit2.Response

fun <T, R> withExceptionHandle(
    request: () -> Response<R>,
    onSuccess: (R) -> T,
    onFail: (Exception) -> T
): T {
    return try {
        val retrofitResponse = request.invoke()
        val body = retrofitResponse.body()

        if (retrofitResponse.isSuccessful && body != null) {
            onSuccess.invoke(body)
        } else {
            throw IllegalStateException("RetrofitResponse is not successful")
        }
    } catch (exception: Exception) {
        onFail.invoke(exception)
    }
}

/*
* API used not basic millis-time. Converts millis-time to standard format
* Example:
*   Before from response: 12345678
*   After:                12345678000
* */
fun Long.toBasicMillisTime(): Long {
    val timeStr = this.toString()
    val resultStr = "${timeStr}000"
    return resultStr.toLong()
}