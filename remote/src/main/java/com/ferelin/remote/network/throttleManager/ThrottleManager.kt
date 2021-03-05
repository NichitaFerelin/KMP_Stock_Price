package com.ferelin.remote.network.throttleManager

import com.ferelin.remote.utilits.Api
import kotlinx.coroutines.*
import java.util.*
import kotlin.collections.HashMap
import kotlin.collections.LinkedHashSet
import kotlin.math.abs

/*
*   The throttle manager queues messages to limit the
*   number of requests per second and to keep the requests
*   up to date. It also helps to avoid duplicate or repeated requests.
*
*   Every request saves in history for one day.
*   ( Repeated request is redundant, because data such as "Price change for the day" will not
*   updated earlier than the next day. The required data must be taken from local database )
*
*   Send params with ignoreHistory::true and request will be invoked
*
*   Requests in queue lose relevance. Depending on this they may not be called.
*   To ignore this send params with eraseIfNotActual::false.
* */

class ThrottleManager {

    private var mCompanyApi: ((String) -> Unit)? = null
    private var mStockCandleApi: ((String) -> Unit)? = null
    private var mStockSymbolsApi: ((String) -> Unit)? = null

    private val mMessagesQueue =
        Collections.synchronizedSet(LinkedHashSet<HashMap<String, Any>>(100))
    private var mMessagesHistory = Collections.synchronizedMap(HashMap<String, Any?>(300, 1F))

    private val mPerSecondRequestLimit = 1000L
    private var mIsRunning = true

    private var mJob: Job? = null

    init {
        mJob = CoroutineScope(Dispatchers.IO).launch { start() }
    }

    fun addMessage(
        symbol: String,
        api: String,
        position: Int = 0,
        eraseIfNotActual: Boolean = true,
        ignoreDuplicate: Boolean = false
    ) {
        if (ignoreDuplicate || isNotDuplicatedMessage(symbol)) {
            acceptMessage(symbol, api, position, eraseIfNotActual)
        }
    }

    fun stop() {
        mIsRunning = false
    }

    fun setUpApi(api: String, func: (String) -> Unit) {
        when (api) {
            Api.COMPANY_PROFILE -> if (mCompanyApi == null) mCompanyApi = func
            Api.STOCK_CANDLE -> if (mStockCandleApi == null) mStockCandleApi = func
            Api.STOCK_SYMBOLS -> if (mStockSymbolsApi == null) mStockSymbolsApi = func
        }
    }

    fun setUpMessagesHistory(map: HashMap<String, Any?>) {
        mMessagesHistory = map
    }

    fun invalidate() {
        mCompanyApi = null
        mStockCandleApi = null
        mStockSymbolsApi = null
        mJob?.cancel()
        mJob = null
        mMessagesQueue.clear()
        mIsRunning = true
    }

    private suspend fun start() {
        while (mIsRunning) {
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
                    Api.COMPANY_PROFILE -> mCompanyApi?.invoke(symbol)
                    Api.STOCK_CANDLE -> mStockCandleApi?.invoke(symbol)
                    Api.STOCK_SYMBOLS -> mStockSymbolsApi?.invoke(symbol)
                    else -> throw IllegalStateException("Unknown throttle manager api: $api")
                }
                mMessagesHistory[symbol] = null
                delay(mPerSecondRequestLimit)
            } ?: delay(200)
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
        return abs(currentPosition - lastPosition) >= 10 && eraseIfNotActual
    }

    companion object {
        private const val sSymbol = "symbol"
        private const val sApi = "api"
        private const val sPosition = "position"
        private const val sEraseState = "erase"
    }
}