package com.ferelin.remote.network.throttleManager

interface ThrottleManagerHelper {

    fun addMessage(
        symbol: String,
        api: String,
        position: Int = 0,
        eraseIfNotActual: Boolean = true,
        ignoreDuplicate: Boolean = false
    )

    fun setUpApi(api: String, func: (String) -> Unit)

    fun invalidate()
}