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
    private companion object {
        const val PER_SECOND_REQUESTS_LIMIT = 1050L
        const val ALREADY_NOT_ACTUAL_REQUEST = 13
    }

    private var stockPriceApi: ((Int, String) -> Unit)? = null

    private val messagesQueue = LinkedHashSet<CachedMessage>(100)
    private var messagesHistory = HashMap<Int, Unit>(300, 0.5F)

    private var isRunning = true

    private var workerJob: Job? = null

    init {
        workerJob = externalScope.launch(dispatchersProvider.IO) {
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
        stockPriceApi = onExecute
    }

    fun invalidate() {
        workerJob?.cancel()
        workerJob = null
        messagesQueue.clear()
        isRunning = false
    }

    private suspend fun start() {
        while (isRunning) {
            try {
                messagesQueue.firstOrNull()?.let { task ->
                    if (messagesHistory[task.companyId] == Unit) {
                        messagesQueue.remove(task)
                        return@let
                    }

                    val lastPosition = messagesQueue.last().keyPosition
                    messagesQueue.remove(task)

                    if (isNotActual(task.keyPosition, lastPosition, task.eraseIfNotActual)) {
                        return@let
                    }

                    Timber.d(
                        "execute request for ${task.companyTicker}" +
                                ". Api: $stockPriceApi"
                    )
                    stockPriceApi?.invoke(task.companyId, task.companyTicker)

                    messagesHistory[task.companyId] = Unit
                    delay(PER_SECOND_REQUESTS_LIMIT)
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
        messagesQueue.add(
            CachedMessage(
                companyId,
                companyTicker,
                position,
                eraseIfNotActual
            )
        )
    }

    private fun isNotDuplicatedMessage(companyId: Int): Boolean {
        return !messagesHistory.containsKey(companyId)
    }

    private fun isNotActual(
        currentPosition: Int,
        lastPosition: Int,
        eraseIfNotActual: Boolean
    ): Boolean {
        return abs(currentPosition - lastPosition) >= ALREADY_NOT_ACTUAL_REQUEST
                && eraseIfNotActual
    }
}