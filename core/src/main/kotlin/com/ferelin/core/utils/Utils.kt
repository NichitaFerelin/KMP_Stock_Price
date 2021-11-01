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

package com.ferelin.core.utils

import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.timerTask

const val SHARING_STOP_TIMEOUT = 5000L

fun withTimer(time: Long = 200L, body: () -> Unit) {
    Timer().schedule(timerTask {
        body.invoke()
    }, time)
}

/*
* Builds profit string. Example:
* Call:    buildProfitString (100.0, 50.0)
* Result:  "+$50.0 (50,0%)"
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

fun Long.toDateStr(): String {
    val datePattern = "dd MMM yyyy"
    val dateFormat = SimpleDateFormat(datePattern, Locale.ENGLISH)
    return dateFormat.format(Date(this)).filter { it != ',' }
}

fun parseMonthFromDate(date: String): String {
    return date.filter { it.isLetter() }
}

fun parseYearFromDate(date: String): String {
    return date.split(" ").getOrNull(2) ?: ""
}

/*
* Builds price string from double
* Call:     adaptPrice(2253.14)
* Result:   $2 253.14
* */
fun Double.toStrPrice(): String {
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