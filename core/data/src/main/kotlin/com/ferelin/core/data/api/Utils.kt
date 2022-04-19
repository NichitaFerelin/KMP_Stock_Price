package com.ferelin.core.data.api

internal fun Long.toUnixTime() : Long {
    return this * 1000
}