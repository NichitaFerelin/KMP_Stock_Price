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
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.math.abs


@Singleton
class RequestsLimiter @Inject constructor(
    @Named("ExternalScope") externalScope: CoroutineScope,
    dispatchersProvider: DispatchersProvider
) {
    private var mStockPriceApi: ((Int, String) -> Unit)? = null

    private val mMessagesQueue = LinkedHashSet<CachedMessage>(100)
    private var mMessagesHistory = HashMap<Int, Unit>(300, 0.5F)

    private var mIsRunning = true

    private var mJob: Job? = null

    private companion object {
        const val sPerSecondRequestLimit = 1050L
        const val sAlreadyNotActualValue = 13
    }

    init {
        mJob = externalScope.launch(dispatchersProvider.IO) {
            start()
        }
    }

    fun addRequestToOrder(
        companyId: Int,
        companyTicker: String,
        keyPosition: Int,
        eraseIfNotActual: Boolean,
        ignoreDuplicates: Boolean
    ) {
        if (ignoreDuplicates || isNotDuplicatedMessage(companyId)) {
            acceptMessage(companyId, companyTicker, keyPosition, eraseIfNotActual)
        }
    }

    fun onExecuteRequest(onExecute: (Int, String) -> Unit) {
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
                mMessagesQueue.firstOrNull()?.let { task ->
                    if (mMessagesHistory[task.companyId] == Unit) {
                        mMessagesQueue.remove(task)
                        return@let
                    }

                    val lastPosition = mMessagesQueue.last().keyPosition
                    mMessagesQueue.remove(task)

                    if (isNotActual(task.keyPosition, lastPosition, task.eraseIfNotActual)) {
                        return@let
                    }

                    Timber.d(
                        "execute request for ${task.companyTicker}" +
                                ". Api: $mStockPriceApi"
                    )
                    mStockPriceApi?.invoke(task.companyId, task.companyTicker)

                    mMessagesHistory[task.companyId] = Unit
                    delay(sPerSecondRequestLimit)
                }
            } catch (exception: ConcurrentModificationException) {
                // Do nothing. Message will not be removed
                Timber.d("execute exception $exception")
            }
        }
    }

    private fun acceptMessage(
        companyId: Int,
        companyTicker: String,
        position: Int,
        eraseIfNotActual: Boolean
    ) {
        mMessagesQueue.add(
            CachedMessage(
                companyId,
                companyTicker,
                position,
                eraseIfNotActual
            )
        )
    }

    private fun isNotDuplicatedMessage(companyId: Int): Boolean {
        return !mMessagesHistory.containsKey(companyId)
    }

    private fun isNotActual(
        currentPosition: Int,
        lastPosition: Int,
        eraseIfNotActual: Boolean
    ): Boolean {
        return abs(currentPosition - lastPosition) >= sAlreadyNotActualValue
                && eraseIfNotActual
    }
}