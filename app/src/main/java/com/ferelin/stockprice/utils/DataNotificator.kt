package com.ferelin.stockprice.utils


sealed class DataNotificator<out T> {
    data class Success<out T>(val data: T) : DataNotificator<T>()
    data class NewItem<out T>(val data: T) : DataNotificator<T>()
    data class Remove<out T>(val data: T) : DataNotificator<T>()
    data class Error<out T>(val message: String, val data: T? = null) : DataNotificator<T>()
    class Loading<out T> : DataNotificator<T>()
}