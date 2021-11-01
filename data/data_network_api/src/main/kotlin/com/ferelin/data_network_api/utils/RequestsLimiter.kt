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

package com.ferelin.data_network_api.utils

import com.ferelin.shared.NAMED_EXTERNAL_SCOPE
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import kotlin.math.abs

/**
 * The API has a limit on the number of requests per minute.
 * This class optimizes the execution of queries by limiting their number per minute
 * and excluding "identical" queries.
 * */
@Singleton
class RequestsLimiter @Inject constructor(
    @Named(NAMED_EXTERNAL_SCOPE) private val externalScope: CoroutineScope
) {
    private var stockPriceApi: ((Int, String) -> Unit)? = null

    // Main queue with requests
    private val requestsQueue = LinkedHashSet<CachedMessage>(100)

    // Requests history with which duplicate requests can be excluded
    private var requestsHistory = HashMap<Int, Unit>(300, 0.5F)

    private var isRunning = true

    private companion object {
        const val perSecondRequestsLimit = 1050L
        const val alreadyNotActualIndex = 13
    }

    init {
        externalScope.launch {
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
        Timber.d("add request to order (company ticker = $companyTicker)")

        if (ignoreDuplicates || isNotDuplicatedMessage(companyId)) {
            acceptMessage(companyId, companyTicker, keyPosition, eraseIfNotActual)
        }
    }

    fun onExecuteRequest(onExecute: (Int, String) -> Unit) {
        stockPriceApi = onExecute
    }

    private suspend fun start() {
        while (isRunning) {
            try {
                requestsQueue.firstOrNull()?.let { task ->
                    Timber.d("task processing $task")

                    // Check if is duplicate
                    if (requestsHistory[task.companyId] == Unit) {
                        Timber.d("remove duplicated task $task")

                        requestsQueue.remove(task)
                        return@let
                    }

                    val lastPosition = requestsQueue.last().keyPosition
                    requestsQueue.remove(task)

                    if (isNotActual(task.keyPosition, lastPosition, task.eraseIfNotActual)) {
                        Timber.d("remove not actual task $task")

                        requestsQueue.remove(task)
                        return@let
                    }

                    externalScope.launch {
                        Timber.d(
                            "execute request for ${task.companyTicker}" +
                                    ". Api: $stockPriceApi"
                        )
                        stockPriceApi?.invoke(task.companyId, task.companyTicker)
                    }

                    requestsHistory[task.companyId] = Unit
                    delay(perSecondRequestsLimit)
                }
            } catch (exception: ConcurrentModificationException) {
                // Do nothing. Request will not be removed
            }
        }
    }

    private fun acceptMessage(
        companyId: Int,
        companyTicker: String,
        position: Int,
        eraseIfNotActual: Boolean
    ) {
        Timber.d("accept message (company ticker = $companyTicker)")

        requestsQueue.add(
            CachedMessage(
                companyId,
                companyTicker,
                position,
                eraseIfNotActual
            )
        )
    }

    private fun isNotDuplicatedMessage(companyId: Int): Boolean {
        return !requestsHistory.containsKey(companyId)
    }

    private fun isNotActual(
        currentPosition: Int,
        lastPosition: Int,
        eraseIfNotActual: Boolean
    ): Boolean {
        return abs(currentPosition - lastPosition) >= alreadyNotActualIndex
                && eraseIfNotActual
    }
}

internal class CachedMessage(
    val companyId: Int,
    val companyTicker: String,
    val keyPosition: Int,
    val eraseIfNotActual: Boolean
) {
    override fun equals(other: Any?): Boolean {
        return if (other is CachedMessage) {
            companyId == other.companyId
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return companyId.hashCode()
    }

    override fun toString(): String {
        return "|cached message: $companyTicker|"
    }
}