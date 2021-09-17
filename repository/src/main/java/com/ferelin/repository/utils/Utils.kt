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

package com.ferelin.repository.utils

import com.ferelin.remote.utils.BaseResponse
import com.ferelin.remote.utils.RESPONSE_ERROR
import com.ferelin.remote.utils.RESPONSE_LIMIT
import com.ferelin.remote.utils.RESPONSE_OK
import java.text.SimpleDateFormat
import java.util.*

suspend fun <T, R> withResponseHandler(
    request: suspend () -> BaseResponse<T>,
    mapper: (T) -> R
): RepositoryResponse<R> {
    val networkResponse = request.invoke()
    return unpackNetworkResponse(networkResponse, mapper)
}

fun <T, R> unpackNetworkResponse(
    networkResponse: BaseResponse<T>,
    mapper: (T) -> R
): RepositoryResponse<R> {
    return if (networkResponse.code == RESPONSE_OK) {
        val responseModel = networkResponse.data!!
        RepositoryResponse.Success(
            owner = networkResponse.owner,
            data = mapper.invoke(responseModel)
        )
    } else {
        when (networkResponse.code) {
            // TODO codes
            RESPONSE_LIMIT -> RepositoryResponse.Failed(RepositoryMessages.ReachedLimit)
            RESPONSE_ERROR -> RepositoryResponse.Failed(RepositoryMessages.Error)
            else -> RepositoryResponse.Failed()
        }
    }
}

/*
    * API used not basic millis-time
    * Example:
    *   Before from response: 12345678
    *   After:  12345678000
    * */
fun Double.toBasicMillisTime(): Long {
    val timeStr = this.toLong().toString()
    val resultStr = "${timeStr}000"
    return resultStr.toLong()
}

/*
    * Call:     adaptPrice(2253.14)
    * Result:   $2 253.14
    * */
fun Double.toUiPrice(): String {
    var resultStr = ""
    val priceStr = this.toString()

    val reminder = priceStr.substringAfter(".")
    var formattedSeparator = "."
    val formattedReminder = when {
        reminder.length > 2 -> reminder.substring(0, 2)
        reminder.last() == '0' -> {
            formattedSeparator = ""
            ""
        }
        else -> reminder
    }

    val integer = priceStr.substringBefore(".")
    var counter = 0
    for (index in integer.length - 1 downTo 0) {
        resultStr += integer[index]
        counter++
        if (counter == 3 && index != 0) {
            resultStr += " "
            counter = 0
        }
    }
    return "$${resultStr.reversed()}$formattedSeparator$formattedReminder"
}

fun Long.toDateStr(): String {
    val datePattern = "dd MMM yyyy"
    val dateFormat = SimpleDateFormat(datePattern, Locale.ENGLISH)
    return dateFormat.format(Date(this)).filter { it != ',' }
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