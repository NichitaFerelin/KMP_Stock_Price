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

import com.ferelin.remote.RESPONSE_LIMIT
import com.ferelin.remote.RESPONSE_OK
import com.ferelin.remote.api.companyNews.CompanyNewsResponse
import com.ferelin.remote.api.companyProfile.CompanyProfileResponse
import com.ferelin.remote.api.stockHistory.StockHistoryResponse
import com.ferelin.remote.api.stockPrice.StockPriceResponse
import com.ferelin.remote.api.stockSymbols.StockSymbolResponse
import com.ferelin.remote.base.BaseResponse
import com.ferelin.repository.adaptiveModels.*
import com.ferelin.repository.converter.adapter.DataAdapter
import com.ferelin.repository.utils.RepositoryMessages
import com.ferelin.repository.utils.RepositoryResponse
import com.ferelin.repository.utils.Time
import com.ferelin.repository.utils.formatPrice
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiConverterImpl @Inject constructor(
    private val mAdapter: DataAdapter
) : ApiConverter {

    override fun convertApiResponseToAdaptiveStockCandles(
        response: BaseResponse<StockHistoryResponse>,
        symbol: String
    ): RepositoryResponse<StockHistory> {
        return if (response.responseCode == RESPONSE_OK) {
            val itemResponse = response.responseData as StockHistoryResponse
            RepositoryResponse.Success(
                owner = symbol,
                data = StockHistory(
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
                RESPONSE_LIMIT -> RepositoryResponse.Failed(RepositoryMessages.Limit)
                else -> RepositoryResponse.Failed()
            }
        }
    }

    override fun convertApiResponseToAdaptiveCompanyProfile(
        response: BaseResponse<CompanyProfileResponse>,
        symbol: String,
    ): RepositoryResponse<CompanyProfile> {
        return if (response.responseCode == RESPONSE_OK) {
            val itemResponse = response.responseData as CompanyProfileResponse
            RepositoryResponse.Success(
                owner = symbol,
                data = CompanyProfile(
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

    override fun convertApiResponseToAdaptiveStockSymbols(
        response: BaseResponse<StockSymbolResponse>
    ): RepositoryResponse<AdaptiveStocksSymbols> {
        return if (response.responseCode == RESPONSE_OK) {
            val itemResponse = response.responseData as StockSymbolResponse
            RepositoryResponse.Success(data = AdaptiveStocksSymbols(itemResponse.stockSymbols))
        } else RepositoryResponse.Failed()
    }

    override fun convertApiResponseToAdaptiveCompanyNews(
        response: BaseResponse<List<CompanyNewsResponse>>,
        symbol: String
    ): RepositoryResponse<CompanyNews> {
        return if (response.responseCode == RESPONSE_OK) {
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
                data = CompanyNews(
                    ids.map { it.toString().substringBefore(".") }.toList(),
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
                RESPONSE_LIMIT -> RepositoryResponse.Failed(RepositoryMessages.Limit)
                else -> RepositoryResponse.Failed()
            }
        }
    }

    override fun convertApiResponseToAdaptiveCompanyDayData(
        response: BaseResponse<StockPriceResponse>
    ): RepositoryResponse<StockPrice> {
        return if (response.responseCode == RESPONSE_OK) {
            val itemResponse = response.responseData as StockPriceResponse
            RepositoryResponse.Success(
                owner = response.additionalMessage,
                data = StockPrice(
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
                RESPONSE_LIMIT -> RepositoryResponse.Failed(RepositoryMessages.Limit)
                else -> RepositoryResponse.Failed(owner = response.additionalMessage)
            }
        }
    }
}