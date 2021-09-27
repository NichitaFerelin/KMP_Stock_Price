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

package com.ferelin.remote.utils

import retrofit2.Response
import java.net.SocketTimeoutException

fun <T, R> withExceptionHandle(
    request: () -> Response<R>,
    onSuccess: (R) -> T,
    onFail: () -> T
): T {
    return try {
        val retrofitResponse = request.invoke()
        val body = retrofitResponse.body()

        if (retrofitResponse.isSuccessful && body != null) {
            onSuccess.invoke(body)
        } else {
            onFail.invoke()
        }
    } catch (exception: SocketTimeoutException) {
        onFail.invoke()
    }
}

/*
    * Builds profit string example:
    *   Call:    buildProfitString (100.0, 50.0)
    *   Result:  "+$50.0 (50,0%)"
    * */
fun buildProfitString(currentPrice: Double, previousPrice: Double): String {
    val numberProfit = currentPrice - previousPrice
    val numberProfitStr = numberProfit.toString()

    val digitNumberProfit = numberProfitStr.substringBefore('.').filter { it.isDigit() }
    val remainderNumberProfit = with(numberProfitStr.substringAfter('.')) {
        if (length >= 2) substring(0, 2) else this
    }

    val percentProfit = (100 * (currentPrice - previousPrice) / currentPrice).toString()
    val digitPercentProfit = percentProfit.substringBefore('.').filter { it.isDigit() }
    val remainderPercentProfit = with(percentProfit.substringAfter('.')) {
        if (length >= 2) substring(0, 2) else this
    }

    val prefix = if (currentPrice > previousPrice) "+" else "-"
    return "$prefix$$digitNumberProfit.$remainderNumberProfit ($digitPercentProfit,$remainderPercentProfit%)"
}