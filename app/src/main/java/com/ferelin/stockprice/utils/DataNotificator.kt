package com.ferelin.stockprice.utils


sealed class DataNotificator<out T>(val data: T? = null) {

    class DataPrepared<out T>(data: T) : DataNotificator<T>(data)

    class NewItemAdded<out T>(data: T) : DataNotificator<T>(data)

    class ItemRemoved<out T>(data: T) : DataNotificator<T>(data)

    class ItemUpdatedDefault<out T>(data: T) : DataNotificator<T>(data)

    class ItemUpdatedQuote<out T>(data: T) : DataNotificator<T>(data)

    class ItemUpdatedLiveTime<out T>(data: T) : DataNotificator<T>(data)

    class Loading<out T> : DataNotificator<T>()
}