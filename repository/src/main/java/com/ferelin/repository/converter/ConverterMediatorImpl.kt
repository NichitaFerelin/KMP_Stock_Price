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
import com.ferelin.local.models.SearchRequest
import com.ferelin.local.responses.CompaniesResponse
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
import com.ferelin.repository.converter.helpers.messagesConverter.MessagesConverter
import com.ferelin.repository.converter.helpers.realtimeConverter.RealtimeDatabaseConverter
import com.ferelin.repository.converter.helpers.searchRequestsConverter.SearchRequestsConverter
import com.ferelin.repository.converter.helpers.webSocketConverter.WebSocketConverter
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.repository.utils.RepositoryResponse
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ConverterMediatorImpl @Inject constructor(
    private val mApiConverter: ApiConverter,
    private val mAuthenticationConverter: AuthenticationConverter,
    private val mCompaniesConverter: CompaniesConverter,
    private val mMessagesConverter: MessagesConverter,
    private val mRealtimeDatabaseConverter: RealtimeDatabaseConverter,
    private val mSearchRequestsConverter: SearchRequestsConverter,
    private val mWebSocketConverter: WebSocketConverter,
    private val mChatsConverter: ChatsConverter
) : ConverterMediator {

    override fun convertApiResponseToAdaptiveStockCandles(
        response: BaseResponse<StockHistoryResponse>,
        symbol: String
    ): RepositoryResponse<AdaptiveCompanyHistory> {
        return mApiConverter.convertApiResponseToAdaptiveStockCandles(response, symbol)
    }

    override fun convertApiResponseToAdaptiveCompanyProfile(
        response: BaseResponse<CompanyProfileResponse>,
        symbol: String
    ): RepositoryResponse<AdaptiveCompanyProfile> {
        return mApiConverter.convertApiResponseToAdaptiveCompanyProfile(response, symbol)
    }

    override fun convertApiResponseToAdaptiveStockSymbols(
        response: BaseResponse<StockSymbolResponse>
    ): RepositoryResponse<AdaptiveStocksSymbols> {
        return mApiConverter.convertApiResponseToAdaptiveStockSymbols(response)
    }

    override fun convertApiResponseToAdaptiveCompanyNews(
        response: BaseResponse<List<CompanyNewsResponse>>,
        symbol: String
    ): RepositoryResponse<AdaptiveCompanyNews> {
        return mApiConverter.convertApiResponseToAdaptiveCompanyNews(response, symbol)
    }

    override fun convertApiResponseToAdaptiveCompanyDayData(
        response: BaseResponse<StockPriceResponse>
    ): RepositoryResponse<AdaptiveCompanyDayData> {
        return mApiConverter.convertApiResponseToAdaptiveCompanyDayData(response)
    }

    override fun convertTryToRegisterResponseToRepositoryResponse(
        response: BaseResponse<Boolean>
    ): RepositoryResponse<Boolean> {
        return mAuthenticationConverter.convertTryToRegisterResponseToRepositoryResponse(response)
    }

    override fun convertAuthenticationResponseToRepositoryResponse(
        response: BaseResponse<Boolean>
    ): RepositoryResponse<RepositoryMessages> {
        return mAuthenticationConverter.convertAuthenticationResponseToRepositoryResponse(response)
    }

    override fun convertLocalCompaniesResponseToRepositoryResponse(
        response: CompaniesResponse
    ): RepositoryResponse<List<AdaptiveCompany>> {
        return mCompaniesConverter.convertLocalCompaniesResponseToRepositoryResponse(response)
    }

    override fun convertAdaptiveCompanyToCompany(company: AdaptiveCompany): Company {
        return mCompaniesConverter.convertAdaptiveCompanyToCompany(company)
    }

    override fun convertRealtimeResponseToRepositoryResponse(
        response: BaseResponse<String?>?
    ): RepositoryResponse<String> {
        return mRealtimeDatabaseConverter.convertRealtimeResponseToRepositoryResponse(response)
    }

    override fun convertAdaptiveRequestToRequest(
        searchRequest: AdaptiveSearchRequest
    ): SearchRequest {
        return mSearchRequestsConverter.convertAdaptiveRequestToRequest(searchRequest)
    }

    override fun convertLocalRequestsResponseToAdaptiveRequests(
        searchRequests: List<SearchRequest>
    ): List<AdaptiveSearchRequest> {
        return mSearchRequestsConverter.convertLocalRequestsResponseToAdaptiveRequests(
            searchRequests
        )
    }

    override fun convertNetworkRequestsResponseToRepositoryResponse(
        response: BaseResponse<HashMap<Int, String>>?
    ): RepositoryResponse<List<AdaptiveSearchRequest>> {
        return mSearchRequestsConverter.convertNetworkRequestsResponseToRepositoryResponse(response)
    }

    override fun convertWebSocketResponseForUi(
        response: BaseResponse<WebSocketResponse>
    ): RepositoryResponse<AdaptiveWebSocketPrice> {
        return mWebSocketConverter.convertWebSocketResponseForUi(response)
    }

    override fun convertAdaptiveChatToChat(adaptiveChat: AdaptiveChat): Chat {
        return mChatsConverter.convertAdaptiveChatToChat(adaptiveChat)
    }

    override fun convertChatsToAdaptiveChats(
        chats: List<Chat>?
    ): RepositoryResponse<List<AdaptiveChat>> {
        return mChatsConverter.convertChatsToAdaptiveChats(chats)
    }

    override fun convertNetworkCompaniesResponseToRepositoryResponse(
        response: BaseResponse<List<String>>?
    ): RepositoryResponse<List<String>> {
        return mCompaniesConverter.convertNetworkCompaniesResponseToRepositoryResponse(response)
    }

    override fun convertChatResponseToRepositoryResponse(
        response: BaseResponse<String>
    ): RepositoryResponse<AdaptiveChat> {
        return mChatsConverter.convertChatResponseToRepositoryResponse(response)
    }

    override fun convertAdaptiveMessageToMessage(adaptiveMessage: AdaptiveMessage): Message {
        return mMessagesConverter.convertAdaptiveMessageToMessage(adaptiveMessage)
    }

    override fun convertNetworkMessagesResponseToRepositoryResponse(
        response: BaseResponse<HashMap<String, Any>>
    ): RepositoryResponse<AdaptiveMessage> {
        return mMessagesConverter.convertNetworkMessagesResponseToRepositoryResponse(response)
    }

    override fun convertLocalMessagesResponseToRepositoryResponse(
        messages: List<Message>?
    ): RepositoryResponse<List<AdaptiveMessage>> {
        return mMessagesConverter.convertLocalMessagesResponseToRepositoryResponse(messages)
    }
}