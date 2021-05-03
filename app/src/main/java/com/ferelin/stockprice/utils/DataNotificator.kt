package com.ferelin.stockprice.utils

/*
* Default class to set data to State/Shared Flow
* */
sealed class DataNotificator<out T>(val data: T? = null) {

    class DataPrepared<out T>(data: T) : DataNotificator<T>(data)

    class DataUpdated<out T>(data: T) : DataNotificator<T>(data)

    class NewItemAdded<out T>(data: T) : DataNotificator<T>(data)

    class ItemRemoved<out T>(data: T) : DataNotificator<T>(data)

    class ItemUpdatedCommon<out T>(data: T) : DataNotificator<T>(data)

    class ItemUpdatedQuote<out T>(data: T) : DataNotificator<T>(data)

    class ItemUpdatedLiveTime<out T>(data: T) : DataNotificator<T>(data)

    class Loading<out T> : DataNotificator<T>()
}