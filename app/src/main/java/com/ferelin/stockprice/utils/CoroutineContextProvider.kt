package com.ferelin.stockprice.utils

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

open class CoroutineContextProvider {

    open val Main: CoroutineContext
        get() = Dispatchers.Main

    open val IO: CoroutineContext
        get() = Dispatchers.IO

    open val Default: CoroutineContext
        get() = Dispatchers.Default
}