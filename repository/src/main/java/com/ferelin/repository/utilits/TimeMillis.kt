package com.ferelin.repository.utilits

object TimeMillis {

    const val ONE_YEAR = 31536000000

    fun convertForRequest(time: Long): Long {
        val timeStr = time.toString()
        val resultStr = timeStr.substring(0, timeStr.length - 3)
        return resultStr.toLong()
    }

    fun convertFromResponse(time: Long): Long {
        val timeStr = time.toString()
        val resultStr = "${timeStr}000"
        return resultStr.toLong()
    }
}