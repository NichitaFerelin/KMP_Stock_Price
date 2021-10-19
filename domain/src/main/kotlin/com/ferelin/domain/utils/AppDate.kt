package com.ferelin.domain.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Special time millis format for API requests
 * */
object AppDate {

    const val ONE_YEAR = 31536000000

    fun toTimeMillisForRequest(time: Long): Long {
        val timeStr = time.toString()
        val resultStr = timeStr.substring(0, timeStr.length - 3)
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