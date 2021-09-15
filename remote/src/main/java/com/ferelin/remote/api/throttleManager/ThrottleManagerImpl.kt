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

package com.ferelin.remote.api.throttleManager

import com.ferelin.remote.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.abs

/**
 * [ThrottleManagerImpl] provides ability to:
 *  - Avoid API limit error.
 *  - Optimize requests to network. Before sending to the network -> request will be
 * checked by condition [isNotActual] -> if method will return true -> request will be deleted
 *
 *   Requests are inserted into a [mMessagesQueue], which are retrieved from there once per/[mPerSecondRequestLimit].
 *   Request will be saved into a [mMessagesHistory] to check future requests.
 *
 *   Is used only to load company quotes, but can be used for any requests type.
 */

@Singleton
class ThrottleManagerImpl @Inject constructor(appScope: CoroutineScope) : ThrottleManager {

    private var mCompanyProfileApi: ((String) -> Unit)? = null
    private var mCompanyNewsApi: ((String) -> Unit)? = null
    private var mStockPriceApi: ((String) -> Unit)? = null
    private var mStockHistoryApi: ((String) -> Unit)? = null
    private var mStockSymbolsApi: ((String) -> Unit)? = null

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
        messageNumber: Int,
        eraseIfNotActual: Boolean,
        ignoreDuplicates: Boolean
    ) {
        if (ignoreDuplicates || isNotDuplicatedMessage(companyOwnerSymbol)) {
            acceptMessage(companyOwnerSymbol, apiTag, messageNumber, eraseIfNotActual)
        }
    }

    override fun setUpApi(apiTag: String, onResponse: (String) -> Unit) {
        when (apiTag) {
            COMPANY_PROFILE -> if (mCompanyProfileApi == null) mCompanyProfileApi = onResponse
            COMPANY_NEWS -> if (mCompanyNewsApi == null) mCompanyNewsApi = onResponse
            COMPANY_QUOTE -> if (mStockPriceApi == null) mStockPriceApi = onResponse
            STOCK_CANDLES -> if (mStockHistoryApi == null) mStockHistoryApi = onResponse
            STOCK_SYMBOLS -> if (mStockSymbolsApi == null) mStockSymbolsApi = onResponse
            else -> throw IllegalStateException("Unknown api for throttleManager: $apiTag")
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

                    when (api) {
                        COMPANY_PROFILE -> mCompanyProfileApi?.invoke(symbol)
                        COMPANY_NEWS -> mCompanyNewsApi?.invoke(symbol)
                        COMPANY_QUOTE -> mStockPriceApi?.invoke(symbol)
                        STOCK_CANDLES -> mStockHistoryApi?.invoke(symbol)
                        STOCK_SYMBOLS -> mStockSymbolsApi?.invoke(symbol)
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