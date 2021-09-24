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

import java.text.SimpleDateFormat
import java.util.*

/*
    * API used not basic millis-time
    * Example:
    *   Before from response: 12345678
    *   After:  12345678000
    * */
fun Long.toBasicMillisTime(): Long {
    val timeStr = this.toString()
    val resultStr = "${timeStr}000"
    return resultStr.toLong()
}

/*
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

fun Long.toDateStr(): String {
    val datePattern = "dd MMM yyyy"
    val dateFormat = SimpleDateFormat(datePattern, Locale.ENGLISH)
    return dateFormat.format(Date(this)).filter { it != ',' }
}