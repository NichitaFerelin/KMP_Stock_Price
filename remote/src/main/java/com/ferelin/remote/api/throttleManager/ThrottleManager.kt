package com.ferelin.remote.api.throttleManager

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

import com.ferelin.remote.utils.Api
import com.ferelin.shared.CoroutineContextProvider
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashSet
import kotlin.math.abs

/**
 * [ThrottleManager] designed to:
 *  - Avoid API limit error.
 *  - Optimize requests to network. Before sending to the network -> request will be
 * checked by condition [isNotActual] -> if method will return true -> request will be deleted
 *
 *   Requests are inserted into a [mMessagesQueue], which are retrieved from there once per/[mPerSecondRequestLimit].
 *   Request will be saved into a [mMessagesHistory] to check future requests.
 *
 *   Is used only to load company quotes, but can be used for any requests type.
 */
class ThrottleManager(
    coroutineContext: CoroutineContextProvider = CoroutineContextProvider()
) : ThrottleManagerHelper {

    private var mCompanyProfileApi: ((String) -> Unit)? = null
    private var mCompanyNewsApi: ((String) -> Unit)? = null
    private var mCompanyQuoteApi: ((String) -> Unit)? = null
    private var mStockCandlesApi: ((String) -> Unit)? = null
    private var mStockSymbolsApi: ((String) -> Unit)? = null

    private val mMessagesQueue =
        Collections.synchronizedSet(LinkedHashSet<HashMap<String, Any>>(100))
    private var mMessagesHistory = Collections.synchronizedMap(HashMap<String, Any?>(300, 0.5F))

    private val mPerSecondRequestLimit = 1050L
    private var mIsRunning = true

    private var mJob: Job? = null

    init {
        mJob = CoroutineScope(coroutineContext.IO).launch { start() }
    }

    override fun addMessage(
        symbol: String,
        api: String,
        position: Int,
        eraseIfNotActual: Boolean,
        ignoreDuplicate: Boolean
    ) {
        if (ignoreDuplicate || isNotDuplicatedMessage(symbol)) {
            acceptMessage(symbol, api, position, eraseIfNotActual)
        }
    }

    override fun setUpApi(api: String, func: (String) -> Unit) {
        when (api) {
            Api.COMPANY_PROFILE -> if (mCompanyProfileApi == null) mCompanyProfileApi = func
            Api.COMPANY_NEWS -> if (mCompanyNewsApi == null) mCompanyNewsApi = func
            Api.COMPANY_QUOTE -> if (mCompanyQuoteApi == null) mCompanyQuoteApi = func
            Api.STOCK_CANDLES -> if (mStockCandlesApi == null) mStockCandlesApi = func
            Api.STOCK_SYMBOLS -> if (mStockSymbolsApi == null) mStockSymbolsApi = func
            else -> throw IllegalStateException("Unknown api for throttleManager: $api")
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
                        Api.COMPANY_PROFILE -> mCompanyProfileApi?.invoke(symbol)
                        Api.COMPANY_NEWS -> mCompanyNewsApi?.invoke(symbol)
                        Api.COMPANY_QUOTE -> mCompanyQuoteApi?.invoke(symbol)
                        Api.STOCK_CANDLES -> mStockCandlesApi?.invoke(symbol)
                        Api.STOCK_SYMBOLS -> mStockSymbolsApi?.invoke(symbol)
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
        return abs(currentPosition - lastPosition) >= 13 && eraseIfNotActual
    }

    companion object {
        private const val sSymbol = "symbol"
        private const val sApi = "api"
        private const val sPosition = "position"
        private const val sEraseState = "erase"
    }
}