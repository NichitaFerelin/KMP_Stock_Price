package com.ferelin.remote.utils

import kotlinx.coroutines.channels.ProducerScope
import kotlinx.coroutines.isActive

fun <E> ProducerScope<E>.offerSafe(element: E) {
    if (isActive) {
        offer(element)
    }
}
