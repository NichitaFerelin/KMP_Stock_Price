package com.ferelin.remote

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

import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.network.NetworkManagerHelper
import com.ferelin.remote.network.companyNews.CompanyNewsResponse
import com.ferelin.remote.network.companyProfile.CompanyProfileResponse
import com.ferelin.remote.network.companyQuote.CompanyQuoteResponse
import com.ferelin.remote.network.stockCandles.StockCandlesResponse
import com.ferelin.remote.network.stockSymbols.StockSymbolResponse
import com.ferelin.remote.webSocket.WebSocketConnectorHelper
import com.ferelin.remote.webSocket.WebSocketResponse
import kotlinx.coroutines.flow.Flow

/*
* Providing requests to right entity
* */
class RemoteMediator(
    private val mNetworkManager: NetworkManagerHelper,
    private val mWebSocketConnector: WebSocketConnectorHelper
) : RemoteMediatorHelper {

    override fun openWebSocketConnection(token: String): Flow<BaseResponse<WebSocketResponse>> {
        return mWebSocketConnector.openWebSocketConnection(token)
    }

    override fun closeWebSocketConnection() {
        mWebSocketConnector.closeWebSocketConnection()
    }

    override fun subscribeItemOnLiveTimeUpdates(symbol: String, previousPrice: Double) {
        mWebSocketConnector.subscribeItemOnLiveTimeUpdates(symbol, previousPrice)
    }

    override fun unsubscribeItemFromLiveTimeUpdates(symbol: String) {
        mWebSocketConnector.unsubscribeItemFromLiveTimeUpdates(symbol)
    }

    override fun loadStockCandles(
        symbol: String,
        from: Long,
        to: Long,
        resolution: String
    ): Flow<BaseResponse<StockCandlesResponse>> {
        return mNetworkManager.loadStockCandles(symbol, from, to, resolution)
    }

    override fun loadCompanyProfile(symbol: String): Flow<BaseResponse<CompanyProfileResponse>> {
        return mNetworkManager.loadCompanyProfile(symbol)
    }

    override fun loadStockSymbols(): Flow<BaseResponse<StockSymbolResponse>> {
        return mNetworkManager.loadStockSymbols()
    }

    override fun loadCompanyNews(
        symbol: String,
        from: String,
        to: String
    ): Flow<BaseResponse<List<CompanyNewsResponse>>> {
        return mNetworkManager.loadCompanyNews(symbol, from, to)
    }

    override fun loadCompanyQuote(
        symbol: String,
        position: Int,
        isImportant: Boolean
    ): Flow<BaseResponse<CompanyQuoteResponse>> {
        return mNetworkManager.loadCompanyQuote(symbol, position, isImportant)
    }
}