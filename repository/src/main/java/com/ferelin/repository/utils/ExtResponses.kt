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

package com.ferelin.repository.utils

import com.ferelin.firebase.auth.AuthenticationResponse
import com.ferelin.local.database.Company
import com.ferelin.remote.networkApi.entities.ActualStockPriceResponse
import com.ferelin.remote.networkApi.entities.CompanyNewsResponse
import com.ferelin.remote.networkApi.entities.StockPriceHistoryResponse
import com.ferelin.remote.webSocket.response.WebSocketResponse
import com.ferelin.repository.useCaseModels.*
import java.util.*

fun Company.toUseCaseCompany(): UseCaseCompany {
    return UseCaseCompany(
        id = this.id,
        companyProfile = CompanyProfile(
            name = this.name,
            symbol = this.symbol,
            logoUrl = this.logoUrl,
            country = this.country,
            phone = this.phone,
            webUrl = this.webUrl,
            industry = this.industry,
            currency = this.currency,
            capitalization = this.capitalization
        ),
        companyDayData = StockPrice(
            currentPrice = this.dayCurrentPrice,
            previousClosePrice = this.dayPreviousClosePrice,
            openPrice = this.dayOpenPrice,
            highPrice = this.dayHighPrice,
            lowPrice = this.dayLowPrice,
            profit = this.dayProfit
        ),
        companyHistory = StockHistory(
            openPrices = this.historyOpenPrices,
            highPrices = this.historyHighPrices,
            lowPrices = this.historyLowPrices,
            closePrices = this.historyClosePrices,
            datePrices = this.historyDatePrices
        ),
        companyNews = CompanyNews(
            dates = this.newsDates,
            headlines = this.newsHeadlines,
            ids = this.newsIds,
            previewImagesUrls = this.newsPreviewImagesUrls,
            sources = this.newsSources,
            summaries = this.newsSummaries,
            browserUrls = this.newsUrls
        ),
        companyStyle = UiStockStyle(),

        isFavourite = this.isFavourite,
        favouriteOrderIndex = this.favouriteOrderIndex,

        isBought = this.isBought,
        boughtPrice = this.boughtPrice
    )
}

fun StockPriceHistoryResponse.toStockHistory(): StockHistory {
    return StockHistory(
        this.openPrices.map { it.toUiPrice() },
        this.highPrices.map { it.toUiPrice() },
        this.lowPrices.map { it.toUiPrice() },
        this.closePrices.map { it.toUiPrice() },
        this.timestamps.map { Time.convertMillisFromResponse(it).toDateStr() }
    )
}

fun List<CompanyNewsResponse>.toCompanyNews(): CompanyNews {
    val ids = LinkedList<String>()
    val headlines = LinkedList<String>()
    val summaries = LinkedList<String>()
    val sources = LinkedList<String>()
    val dateTimes = LinkedList<String>()
    val previewImageUrls = LinkedList<String>()
    val browserUrls = LinkedList<String>()

    this.forEach {
        ids.add(it.toString().substringBefore("."))
        headlines.add(it.headline)
        summaries.add(it.newsSummary)
        sources.add(it.newsSource)
        dateTimes.add(it.dateTime.toBasicMillisTime().toDateStr())
        previewImageUrls.add(it.previewImageUrl)
        browserUrls.add(it.newsBrowserUrl)
    }

    return CompanyNews(
        ids = ids.toList(),
        headlines = headlines.toList(),
        summaries = summaries.toList(),
        sources = sources.toList(),
        dates = dateTimes.toList(),
        browserUrls = browserUrls.toList(),
        previewImagesUrls = previewImageUrls.toList()
    )
}

fun ActualStockPriceResponse.toStockPrice(): StockPrice {
    return StockPrice(
        currentPrice = this.currentPrice.toUiPrice(),
        previousClosePrice = this.previousClosePrice.toUiPrice(),
        openPrice = this.openPrice.toUiPrice(),
        highPrice = this.highPrice.toUiPrice(),
        lowPrice = this.lowPrice.toUiPrice(),
        profit = buildProfitString(this.currentPrice, this.openPrice)
    )
}

fun WebSocketResponse.toLiveTimePrice(): LiveTimePrice {
    return LiveTimePrice(
        owner = symbol,
        price = this.lastPrice.toUiPrice(),
        // TODO
        profit = buildProfitString(this.lastPrice, 0.0)
    )
}

fun AuthenticationResponse.toRepositoryMessage(): RepositoryMessages {
    return when (this) {
        AuthenticationResponse.CodeSent -> RepositoryMessages.CodeSent
        AuthenticationResponse.Complete -> RepositoryMessages.Complete
        AuthenticationResponse.TooManyRequests -> RepositoryMessages.TooManyRequests
        else -> RepositoryMessages.Error
    }
}