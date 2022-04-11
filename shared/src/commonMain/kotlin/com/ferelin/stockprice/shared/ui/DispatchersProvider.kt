package com.ferelin.stockprice.shared.ui

import kotlinx.coroutines.Dispatchers
import kotlin.coroutines.CoroutineContext

open class DispatchersProvider internal constructor() {
    open val Main: CoroutineContext = Dispatchers.Main
    open val IO: CoroutineContext = Dispatchers.IO
    open val Default: CoroutineContext = Dispatchers.Default
}