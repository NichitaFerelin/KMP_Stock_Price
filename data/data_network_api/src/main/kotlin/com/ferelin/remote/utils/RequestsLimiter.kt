/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ferelin.remote.utils

import com.ferelin.shared.DispatchersProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class RequestsLimiter @Inject constructor(
    @Named("ExternalScope") externalScope: CoroutineScope,
    dispatchersProvider: DispatchersProvider
) {
    private var mStockPriceApi: ((String) -> Unit)? = null

    private val mMessagesQueue = LinkedHashSet<HashMap<String, Any>>(100)
    private var mMessagesHistory = HashMap<String, Any?>(300, 0.5F)

    private val mPerSecondRequestLimit = 1050L
    private var mIsRunning = true

    private var mJob: Job? = null

    init {
        mJob = externalScope.launch(dispatchersProvider.IO) {
            start()
        }
    }

    fun addRequestToOrder(
        companyTicker: String,
        keyPosition: Int,
        eraseIfNotActual: Boolean,
        ignoreDuplicates: Boolean
    ) {
        if (ignoreDuplicates || isNotDuplicatedMessage(companyTicker)) {
            acceptMessage(companyTicker, keyPosition, eraseIfNotActual)
        }
    }

    fun onExecuteRequest(onExecute: (String) -> Unit) {
        mStockPriceApi = onExecute
    }

    fun invalidate() {
        mJob?.cancel()
        mJob = null
        mMessagesQueue.clear()
        mIsRunning = false
    }

    private suspend fun start() {
        while (mIsRunning) {
            try {
                mMessagesQueue.firstOrNull()?.let {
                    val lastPosition = mMessagesQueue.last()[sPosition] as Int
                    val currentPosition = it[sPosition] as Int
                    val symbol = it[sSymbol] as String
                    val eraseIfNotActual = it[sEraseState] as Boolean
                    mMessagesQueue.remove(it)

                    if (isNotActual(currentPosition, lastPosition, eraseIfNotActual)) {
                        return@let
                    }

                    mStockPriceApi?.invoke(symbol)

                    mMessagesHistory[symbol] = null
                    delay(mPerSecondRequestLimit)
                }
            } catch (exception: ConcurrentModificationException) {
                // Do nothing. Message will not be removed
            }
        }
    }

    private fun acceptMessage(
        symbol: String,
        position: Int,
        eraseIfNotActual: Boolean
    ) {
        mMessagesQueue.add(
            hashMapOf(
                sSymbol to symbol,
                sPosition to position,
                sEraseState to eraseIfNotActual
            )
        )
    }

    private fun isNotDuplicatedMessage(symbol: String): Boolean {
        return !mMessagesHistory.containsKey(symbol)
    }

    private fun isNotActual(
        currentPosition: Int,
        lastPosition: Int,
        eraseIfNotActual: Boolean
    ): Boolean {
        return abs(currentPosition - lastPosition) >= sAlreadyNotActualValue && eraseIfNotActual
    }

    private companion object {
        const val sAlreadyNotActualValue = 13

        const val sSymbol = "symbol"
        const val sPosition = "position"
        const val sEraseState = "erase"
    }
}