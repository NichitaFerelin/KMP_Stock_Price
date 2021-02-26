package com.ferelin.repository.tools.remote

import com.ferelin.remote.network.companyProfile.CompanyProfileResponse
import com.ferelin.remote.network.stockCandle.StockCandleResponse
import com.ferelin.remote.webSocket.WebSocketResponse
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.utilits.Response
import java.util.*

interface ResponsesConfiguratorHelper {

    fun configure(response: WebSocketResponse, currency: String): Response<HashMap<String, Any>>

    fun configure(symbol: String, response: StockCandleResponse): Response<HashMap<String, Any>>

    fun configure(symbol: String, response: CompanyProfileResponse): Response<AdaptiveCompany>
}