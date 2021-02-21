package com.ferelin.repository.tools.remote

import com.ferelin.local.model.Company
import com.ferelin.remote.network.companyProfile.CompanyProfileResponse
import com.ferelin.remote.network.stockCandle.StockCandleResponse
import com.ferelin.remote.webSocket.WebSocketResponse
import com.ferelin.repository.utilits.Response

interface ResponsesConfiguratorHelper {

    fun configure(response: WebSocketResponse): Response<HashMap<String, Any>>

    fun configure(symbol: String, response: StockCandleResponse): Response<HashMap<String, Any>>

    fun configure(symbol: String, response: CompanyProfileResponse): Response<Company>
}