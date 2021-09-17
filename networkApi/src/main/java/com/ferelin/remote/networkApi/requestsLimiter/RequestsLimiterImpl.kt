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

package com.ferelin.remote.networkApi.requestsLimiter

import com.ferelin.remote.utils.COMPANY_ACTUAL_PRICE
import com.ferelin.remote.utils.COMPANY_NEWS
import com.ferelin.remote.utils.PRICE_CHANGES_HISTORY
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

@Singleton
class RequestsLimiterImpl @Inject constructor(
    appScope: CoroutineScope
) : RequestsLimiter {

    private var mCompanyNewsApi: ((String) -> Unit)? = null
    private var mActualStockPriceApi: ((String) -> Unit)? = null
    private var mPriceChangesHistoryApi: ((String) -> Unit)? = null

    private val mMessagesQueue = LinkedHashSet<HashMap<String, Any>>(100)
    private var mMessagesHistory = HashMap<String, Any?>(300, 0.5F)

    private val mPerSecondRequestLimit = 1050L
    private var mIsRunning = true

    private var mJob: Job? = null

    init {
        mJob = appScope.launch { start() }
    }

    override fun addRequestToOrder(
        companyOwnerSymbol: String,
        apiTag: String,
        keyPosition: Int,
        eraseIfNotActual: Boolean,
        ignoreDuplicates: Boolean
    ) {
        if (ignoreDuplicates || isNotDuplicatedMessage(companyOwnerSymbol)) {
            acceptMessage(companyOwnerSymbol, apiTag, keyPosition, eraseIfNotActual)
        }
    }

    override fun setUpApi(apiTag: String, onResponse: (String) -> Unit) {
        when (apiTag) {
            COMPANY_NEWS -> {
                if (mCompanyNewsApi == null) {
                    mCompanyNewsApi = onResponse
                }
            }
            COMPANY_ACTUAL_PRICE -> {
                if (mActualStockPriceApi == null) {
                    mActualStockPriceApi = onResponse
                }
            }
            PRICE_CHANGES_HISTORY -> {
                if (mPriceChangesHistoryApi == null) {
                    mPriceChangesHistoryApi = onResponse
                }
            }
            else -> throw IllegalStateException("Unknown api tag for [RequestLimiter]: $apiTag")
        }
    }

    override fun invalidate() {
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
                    val api = it[sApi] as String
                    val eraseIfNotActual = it[sEraseState] as Boolean
                    mMessagesQueue.remove(it)

                    if (isNotActual(currentPosition, lastPosition, eraseIfNotActual)) {
                        return@let
                    }

                    // TODO check if api null and do not reset message
                    when (api) {
                        COMPANY_NEWS -> mCompanyNewsApi?.invoke(symbol)
                        COMPANY_ACTUAL_PRICE -> mActualStockPriceApi?.invoke(symbol)
                        PRICE_CHANGES_HISTORY -> mPriceChangesHistoryApi?.invoke(symbol)
                    }
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
        api: String,
        position: Int,
        eraseIfNotActual: Boolean
    ) {
        mMessagesQueue.add(
            hashMapOf(
                sSymbol to symbol,
                sApi to api,
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
        const val sApi = "api"
        const val sPosition = "position"
        const val sEraseState = "erase"
    }
}