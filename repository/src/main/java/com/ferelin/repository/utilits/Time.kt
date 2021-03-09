package com.ferelin.repository.utilits

import java.text.SimpleDateFormat
import java.util.*

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

    fun getDataForRequest(): Pair<String, String> {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.ROOT)
        val currentTimeMillis = System.currentTimeMillis()
        val yearAgoTimeMillis = currentTimeMillis - ONE_YEAR
        val date = Date(currentTimeMillis)
        val yearAgoDate = Date(yearAgoTimeMillis)
        return Pair(dateFormat.format(date), dateFormat.format(yearAgoDate))
    }
}