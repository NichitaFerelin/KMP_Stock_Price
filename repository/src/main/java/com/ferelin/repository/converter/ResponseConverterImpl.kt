package com.ferelin.repository.converter

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

import com.ferelin.local.models.Company
import com.ferelin.local.models.MessagesHolder
import com.ferelin.local.responses.CompaniesResponse
import com.ferelin.local.responses.SearchesResponse
import com.ferelin.remote.api.companyNews.CompanyNewsResponse
import com.ferelin.remote.api.companyProfile.CompanyProfileResponse
import com.ferelin.remote.api.companyQuote.CompanyQuoteResponse
import com.ferelin.remote.api.stockCandles.StockCandlesResponse
import com.ferelin.remote.api.stockSymbols.StockSymbolResponse
import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.webSocket.response.WebSocketResponse
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.converter.helpers.apiConverter.ApiResponseConverter
import com.ferelin.repository.converter.helpers.authenticationConverter.AuthenticationResponseConverter
import com.ferelin.repository.converter.helpers.companiesConverter.CompaniesResponseConverter
import com.ferelin.repository.converter.helpers.firstTimeLaunchConverter.FirstTimeLaunchConverter
import com.ferelin.repository.converter.helpers.messagesConverter.MessagesResponseConverter
import com.ferelin.repository.converter.helpers.realtimeConverter.RealtimeDatabaseConverter
import com.ferelin.repository.converter.helpers.searchRequestsConverter.SearchRequestsConverter
import com.ferelin.repository.converter.helpers.webSocketConverter.WebSocketConverter
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.repository.utils.RepositoryResponse
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [ResponseConverterImpl] is used to convert responses for UI from local/remote modules.
 */
@Singleton
class ResponseConverterImpl @Inject constructor(
    private val mApiResponseConverter: ApiResponseConverter,
    private val mAuthenticationResponseConverter: AuthenticationResponseConverter,
    private val mCompaniesResponseConverter: CompaniesResponseConverter,
    private val mFirstTimeLaunchConverter: FirstTimeLaunchConverter,
    private val mMessagesResponseConverter: MessagesResponseConverter,
    private val mRealtimeDatabaseConverter: RealtimeDatabaseConverter,
    private val mSearchRequestsConverter: SearchRequestsConverter,
    private val mWebSocketConverter: WebSocketConverter
) : ResponseConverter {

    override fun convertStockCandlesResponseForUi(
        response: BaseResponse<StockCandlesResponse>,
        symbol: String
    ): RepositoryResponse<AdaptiveCompanyHistory> {
        return mApiResponseConverter.convertStockCandlesResponseForUi(response, symbol)
    }

    override fun convertCompanyProfileResponseForUi(
        response: BaseResponse<CompanyProfileResponse>,
        symbol: String
    ): RepositoryResponse<AdaptiveCompanyProfile> {
        return mApiResponseConverter.convertCompanyProfileResponseForUi(response, symbol)
    }

    override fun convertStockSymbolsResponseForUi(
        response: BaseResponse<StockSymbolResponse>
    ): RepositoryResponse<AdaptiveStocksSymbols> {
        return mApiResponseConverter.convertStockSymbolsResponseForUi(response)
    }

    override fun convertCompanyNewsResponseForUi(
        response: BaseResponse<List<CompanyNewsResponse>>,
        symbol: String
    ): RepositoryResponse<AdaptiveCompanyNews> {
        return mApiResponseConverter.convertCompanyNewsResponseForUi(response, symbol)
    }

    override fun convertCompanyQuoteResponseForUi(
        response: BaseResponse<CompanyQuoteResponse>
    ): RepositoryResponse<AdaptiveCompanyDayData> {
        return mApiResponseConverter.convertCompanyQuoteResponseForUi(response)
    }

    override fun convertTryToRegisterResponseForUi(
        response: BaseResponse<Boolean>
    ): RepositoryResponse<Boolean> {
        return mAuthenticationResponseConverter.convertTryToRegisterResponseForUi(response)
    }

    override fun convertAuthenticationResponseForUi(
        response: BaseResponse<Boolean>
    ): RepositoryResponse<RepositoryMessages> {
        return mAuthenticationResponseConverter.convertAuthenticationResponseForUi(response)
    }

    override fun convertCompaniesResponseForUi(
        response: CompaniesResponse
    ): RepositoryResponse<List<AdaptiveCompany>> {
        return mCompaniesResponseConverter.convertCompaniesResponseForUi(response)
    }

    override fun convertCompanyForLocal(company: AdaptiveCompany): Company {
        return mCompaniesResponseConverter.convertCompanyForLocal(company)
    }

    override fun convertFirstTimeLaunchStateForUi(state: Boolean?): RepositoryResponse<Boolean> {
        return mFirstTimeLaunchConverter.convertFirstTimeLaunchStateForUi(state)
    }

    override fun convertMessageForLocal(messagesHolder: AdaptiveMessagesHolder): MessagesHolder {
        return mMessagesResponseConverter.convertMessageForLocal(messagesHolder)
    }

    override fun convertRemoteMessagesResponseForUi(
        response: BaseResponse<List<HashMap<String, String>>>
    ): RepositoryResponse<AdaptiveMessagesHolder> {
        return mMessagesResponseConverter.convertRemoteMessagesResponseForUi(response)
    }

    override fun convertRealtimeDatabaseResponseForUi(
        response: BaseResponse<String?>
    ): RepositoryResponse<String> {
        return mRealtimeDatabaseConverter.convertRealtimeDatabaseResponseForUi(response)
    }

    override fun convertUserRelationsResponseForUi(
        response: BaseResponse<List<String>>
    ): RepositoryResponse<List<String>> {
        return mRealtimeDatabaseConverter.convertUserRelationsResponseForUi(response)
    }

    override fun convertSearchesForLocal(search: List<AdaptiveSearchRequest>): Set<String> {
        return mSearchRequestsConverter.convertSearchesForLocal(search)
    }

    override fun convertSearchesForUi(
        response: SearchesResponse
    ): RepositoryResponse<List<AdaptiveSearchRequest>> {
        return mSearchRequestsConverter.convertSearchesForUi(response)
    }

    override fun convertWebSocketResponseForUi(
        response: BaseResponse<WebSocketResponse>
    ): RepositoryResponse<AdaptiveWebSocketPrice> {
        return mWebSocketConverter.convertWebSocketResponseForUi(response)
    }
}