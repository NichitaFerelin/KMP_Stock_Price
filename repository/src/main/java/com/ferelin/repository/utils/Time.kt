package com.ferelin.repository.utils

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

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("NewApi")
object Time {

    const val ONE_YEAR = 31536000000

    fun convertMillisForRequest(time: Long): Long {
        val timeStr = time.toString()
        val resultStr = timeStr.substring(0, timeStr.length - 3)
        return resultStr.toLong()
    }

    fun convertMillisFromResponse(time: Long): Long {
        val timeStr = time.toString()
        val resultStr = "${timeStr}000"
        return resultStr.toLong()
    }

    fun getCurrentDateForRequest(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
        val currentTimeMillis = System.currentTimeMillis()
        val date = Date(currentTimeMillis)
        return dateFormat.format(date)
    }

    fun getYearAgoDateForRequest(): String {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
        val currentTimeMillis = System.currentTimeMillis()
        val yearAgoTimeMillis = currentTimeMillis - ONE_YEAR
        val yearAgoDate = Date(yearAgoTimeMillis)
        return dateFormat.format(yearAgoDate)
    }
}