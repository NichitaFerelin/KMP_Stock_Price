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

import com.ferelin.local.models.Chat
import com.ferelin.local.models.Company
import com.ferelin.local.models.Message
import com.ferelin.local.responses.CompaniesResponse
import com.ferelin.local.responses.SearchesResponse
import com.ferelin.remote.api.companyNews.CompanyNewsResponse
import com.ferelin.remote.api.companyProfile.CompanyProfileResponse
import com.ferelin.remote.api.stockHistory.StockHistoryResponse
import com.ferelin.remote.api.stockPrice.StockPriceResponse
import com.ferelin.remote.api.stockSymbols.StockSymbolResponse
import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.webSocket.response.WebSocketResponse
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.converter.helpers.apiConverter.ApiConverter
import com.ferelin.repository.converter.helpers.authenticationConverter.AuthenticationConverter
import com.ferelin.repository.converter.helpers.chatsConverter.ChatsConverter
import com.ferelin.repository.converter.helpers.companiesConverter.CompaniesConverter
import com.ferelin.repository.converter.helpers.firstTimeLaunchConverter.FirstTimeLaunchConverter
import com.ferelin.repository.converter.helpers.messagesConverter.MessagesConverter
import com.ferelin.repository.converter.helpers.realtimeConverter.RealtimeDatabaseConverter
import com.ferelin.repository.converter.helpers.searchRequestsConverter.SearchRequestsConverter
import com.ferelin.repository.converter.helpers.webSocketConverter.WebSocketConverter
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.repository.utils.RepositoryResponse
import javax.inject.Inject
import javax.inject.Singleton

/**
 * [ConverterMediatorImpl] is used to convert responses for UI from local/remote modules.
 */
@Singleton
class ConverterMediatorImpl @Inject constructor(
    private val mApiConverter: ApiConverter,
    private val mAuthenticationConverter: AuthenticationConverter,
    private val mCompaniesConverter: CompaniesConverter,
    private val mFirstTimeLaunchConverter: FirstTimeLaunchConverter,
    private val mMessagesConverter: MessagesConverter,
    private val mRealtimeDatabaseConverter: RealtimeDatabaseConverter,
    private val mSearchRequestsConverter: SearchRequestsConverter,
    private val mWebSocketConverter: WebSocketConverter,
    private val mChatsConverter: ChatsConverter
) : ConverterMediator {

    override fun fromNetworkResponseToAdaptiveStockCandles(
        response: BaseResponse<StockHistoryResponse>,
        symbol: String
    ): RepositoryResponse<AdaptiveCompanyHistory> {
        return mApiConverter.fromNetworkResponseToAdaptiveStockCandles(response, symbol)
    }

    override fun fromNetworkResponseToAdaptiveCompanyProfile(
        response: BaseResponse<CompanyProfileResponse>,
        symbol: String
    ): RepositoryResponse<AdaptiveCompanyProfile> {
        return mApiConverter.fromNetworkResponseToAdaptiveCompanyProfile(response, symbol)
    }

    override fun fromNetworkResponseToAdaptiveStockSymbols(
        response: BaseResponse<StockSymbolResponse>
    ): RepositoryResponse<AdaptiveStocksSymbols> {
        return mApiConverter.fromNetworkResponseToAdaptiveStockSymbols(response)
    }

    override fun fromNetworkResponseToAdaptiveCompanyNews(
        response: BaseResponse<List<CompanyNewsResponse>>,
        symbol: String
    ): RepositoryResponse<AdaptiveCompanyNews> {
        return mApiConverter.fromNetworkResponseToAdaptiveCompanyNews(response, symbol)
    }

    override fun fromNetworkResponseToAdaptiveCompanyDayData(
        response: BaseResponse<StockPriceResponse>
    ): RepositoryResponse<AdaptiveCompanyDayData> {
        return mApiConverter.fromNetworkResponseToAdaptiveCompanyDayData(response)
    }

    override fun convertTryToRegisterResponseForUi(
        response: BaseResponse<Boolean>
    ): RepositoryResponse<Boolean> {
        return mAuthenticationConverter.convertTryToRegisterResponseForUi(response)
    }

    override fun convertAuthenticationResponseForUi(
        response: BaseResponse<Boolean>
    ): RepositoryResponse<RepositoryMessages> {
        return mAuthenticationConverter.convertAuthenticationResponseForUi(response)
    }

    override fun convertCompaniesResponseForUi(
        response: CompaniesResponse
    ): RepositoryResponse<List<AdaptiveCompany>> {
        return mCompaniesConverter.convertCompaniesResponseForUi(response)
    }

    override fun convertCompanyForLocal(company: AdaptiveCompany): Company {
        return mCompaniesConverter.convertCompanyForLocal(company)
    }

    override fun convertFirstTimeLaunchStateForUi(state: Boolean?): RepositoryResponse<Boolean> {
        return mFirstTimeLaunchConverter.convertFirstTimeLaunchStateForUi(state)
    }

    override fun convertRealtimeDatabaseResponseForUi(
        response: BaseResponse<String?>?
    ): RepositoryResponse<String> {
        return mRealtimeDatabaseConverter.convertRealtimeDatabaseResponseForUi(response)
    }

    override fun convertSearchRequestsForLocal(search: List<AdaptiveSearchRequest>): Set<String> {
        return mSearchRequestsConverter.convertSearchRequestsForLocal(search)
    }

    override fun convertSearchRequestsForUi(
        response: SearchesResponse
    ): RepositoryResponse<List<AdaptiveSearchRequest>> {
        return mSearchRequestsConverter.convertSearchRequestsForUi(response)
    }

    override fun convertWebSocketResponseForUi(
        response: BaseResponse<WebSocketResponse>
    ): RepositoryResponse<AdaptiveWebSocketPrice> {
        return mWebSocketConverter.convertWebSocketResponseForUi(response)
    }

    override fun convertAdaptiveChatForLocal(adaptiveChat: AdaptiveChat): Chat {
        return mChatsConverter.convertAdaptiveChatForLocal(adaptiveChat)
    }

    override fun convertLocalChatsForUi(chats: List<Chat>?): RepositoryResponse<List<AdaptiveChat>> {
        return mChatsConverter.convertLocalChatsForUi(chats)
    }

    override fun convertCompaniesIdsForUi(response: BaseResponse<List<String>>?): RepositoryResponse<List<String>> {
        return mCompaniesConverter.convertCompaniesIdsForUi(response)
    }

    override fun convertSearchRequestsTextForUi(response: BaseResponse<List<String>>?): RepositoryResponse<List<String>> {
        return mSearchRequestsConverter.convertSearchRequestsTextForUi(response)
    }

    override fun convertRemoteChatResponseForUi(
        response: BaseResponse<String>
    ): RepositoryResponse<AdaptiveChat> {
        return mChatsConverter.convertRemoteChatResponseForUi(response)
    }

    override fun convertMessageForLocal(adaptiveMessage: AdaptiveMessage): Message {
        return mMessagesConverter.convertMessageForLocal(adaptiveMessage)
    }

    override fun convertRemoteMessageResponseForUi(
        response: BaseResponse<HashMap<String, Any>>
    ): RepositoryResponse<AdaptiveMessage> {
        return mMessagesConverter.convertRemoteMessageResponseForUi(response)
    }

    override fun convertLocalMessagesResponseForUi(
        messages: List<Message>?
    ): RepositoryResponse<List<AdaptiveMessage>> {
        return mMessagesConverter.convertLocalMessagesResponseForUi(messages)
    }
}