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

package com.ferelin.repository.converter.helpers.apiConverter

import com.ferelin.remote.api.companyNews.CompanyNewsResponse
import com.ferelin.remote.api.companyProfile.CompanyProfileResponse
import com.ferelin.remote.api.companyQuote.CompanyQuoteResponse
import com.ferelin.remote.api.stockCandles.StockCandlesResponse
import com.ferelin.remote.api.stockSymbols.StockSymbolResponse
import com.ferelin.remote.base.BaseResponse
import com.ferelin.remote.utils.Api
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.converter.adapter.DataAdapter
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.repository.utils.Time
import com.ferelin.shared.formatPrice
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiConverterImpl @Inject constructor(
    private val mAdapter: DataAdapter
) : ApiConverter {

    override fun convertStockCandlesResponseForUi(
        response: BaseResponse<StockCandlesResponse>,
        symbol: String
    ): RepositoryResponse<AdaptiveCompanyHistory> {
        return if (response.responseCode == Api.RESPONSE_OK) {
            val itemResponse = response.responseData as StockCandlesResponse
            RepositoryResponse.Success(
                owner = symbol,
                data = AdaptiveCompanyHistory(
                    itemResponse.openPrices.map { formatPrice(it) },
                    itemResponse.highPrices.map { formatPrice(it) },
                    itemResponse.lowPrices.map { formatPrice(it) },
                    itemResponse.closePrices.map { formatPrice(it) },
                    itemResponse.timestamps.map {
                        mAdapter.fromLongToDateStr(Time.convertMillisFromResponse(it))
                    }
                )
            )
        } else {
            when (response.responseCode) {
                Api.RESPONSE_LIMIT -> RepositoryResponse.Failed(RepositoryMessages.Limit)
                else -> RepositoryResponse.Failed()
            }
        }
    }

    override fun convertCompanyProfileResponseForUi(
        response: BaseResponse<CompanyProfileResponse>,
        symbol: String,
    ): RepositoryResponse<AdaptiveCompanyProfile> {
        return if (response.responseCode == Api.RESPONSE_OK) {
            val itemResponse = response.responseData as CompanyProfileResponse
            RepositoryResponse.Success(
                owner = symbol,
                data = AdaptiveCompanyProfile(
                    mAdapter.adaptName(itemResponse.name),
                    symbol,
                    itemResponse.logoUrl,
                    itemResponse.country,
                    mAdapter.adaptPhone(itemResponse.phone),
                    itemResponse.webUrl,
                    itemResponse.industry,
                    itemResponse.currency,
                    formatPrice(itemResponse.capitalization)
                )
            )
        } else RepositoryResponse.Failed()
    }

    override fun convertStockSymbolsResponseForUi(
        response: BaseResponse<StockSymbolResponse>
    ): RepositoryResponse<AdaptiveStocksSymbols> {
        return if (response.responseCode == Api.RESPONSE_OK) {
            val itemResponse = response.responseData as StockSymbolResponse
            RepositoryResponse.Success(data = AdaptiveStocksSymbols(itemResponse.stockSymbols))
        } else RepositoryResponse.Failed()
    }

    override fun convertCompanyNewsResponseForUi(
        response: BaseResponse<List<CompanyNewsResponse>>,
        symbol: String
    ): RepositoryResponse<AdaptiveCompanyNews> {
        return if (response.responseCode == Api.RESPONSE_OK) {
            val itemResponse = response.responseData as List<CompanyNewsResponse>

            val ids = mutableListOf<Double>()
            val headlines = mutableListOf<String>()
            val summaries = mutableListOf<String>()
            val sources = mutableListOf<String>()
            val dateTimes = mutableListOf<Double>()
            val previewImageUrls = mutableListOf<String>()
            val browserUrls = mutableListOf<String>()

            itemResponse.forEach {
                ids.add(it.newsId)
                headlines.add(it.headline)
                summaries.add(it.newsSummary)
                sources.add(it.newsSource)
                dateTimes.add(it.dateTime)
                previewImageUrls.add(it.previewImageUrl)
                browserUrls.add(it.newsBrowserUrl)
            }

            RepositoryResponse.Success(
                owner = symbol,
                data = AdaptiveCompanyNews(
                    ids.map {
                        it.toString().substringBefore(".")
                    }.toList(),
                    headlines.toList(),
                    summaries.toList(),
                    sources.toList(),
                    dateTimes.map {
                        mAdapter.fromLongToDateStr(mAdapter.convertMillisFromResponse(it.toLong()))
                    }.toList(),
                    browserUrls.toList(),
                    previewImageUrls.toList()
                )
            )
        } else {
            when (response.responseCode) {
                Api.RESPONSE_LIMIT -> RepositoryResponse.Failed(RepositoryMessages.Limit)
                else -> RepositoryResponse.Failed()
            }
        }
    }

    override fun convertCompanyQuoteResponseForUi(
        response: BaseResponse<CompanyQuoteResponse>
    ): RepositoryResponse<AdaptiveCompanyDayData> {
        return if (response.responseCode == Api.RESPONSE_OK) {
            val itemResponse = response.responseData as CompanyQuoteResponse
            RepositoryResponse.Success(
                owner = response.additionalMessage,
                data = AdaptiveCompanyDayData(
                    formatPrice(itemResponse.currentPrice),
                    formatPrice(itemResponse.previousClosePrice),
                    formatPrice(itemResponse.openPrice),
                    formatPrice(itemResponse.highPrice),
                    formatPrice(itemResponse.lowPrice),
                    mAdapter.buildProfitString(itemResponse.currentPrice, itemResponse.openPrice)
                )
            )
        } else {
            when (response.responseCode) {
                Api.RESPONSE_LIMIT -> RepositoryResponse.Failed(RepositoryMessages.Limit)
                else -> RepositoryResponse.Failed(owner = response.additionalMessage)
            }
        }
    }
}