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

package com.ferelin.repository.useCaseModels

import com.ferelin.local.database.Company

data class UseCaseCompany(
    val id: Int,

    var companyProfile: CompanyProfile,
    var companyDayData: StockPrice,
    var companyHistory: StockHistory,
    var companyNews: CompanyNews,
    var companyStyle: UiStockStyle,
    var isFavourite: Boolean = false,
    var favouriteOrderIndex: Int = 0,
    var isBought: Boolean = false,
    var boughtPrice: Double = 0.0
) {
    override fun equals(other: Any?): Boolean {
        return if (other is UseCaseCompany) {
            id == other.id
        } else false
    }

    override fun hashCode(): Int {
        return id.hashCode()
    }

    override fun toString(): String {
        return companyProfile.symbol
    }

    fun toDefaultCompany(): Company {
        return Company(
            id = id,

            name = companyProfile.name,

            symbol = companyProfile.symbol,
            logoUrl = companyProfile.logoUrl,
            country = companyProfile.country,
            phone = companyProfile.phone,
            webUrl = companyProfile.webUrl,
            industry = companyProfile.industry,
            currency = companyProfile.currency,
            capitalization = companyProfile.capitalization,

            dayCurrentPrice = companyDayData.currentPrice,
            dayPreviousClosePrice = companyDayData.previousClosePrice,
            dayOpenPrice = companyDayData.openPrice,
            dayHighPrice = companyDayData.highPrice,
            dayLowPrice = companyDayData.lowPrice,
            dayProfit = companyDayData.profit,

            historyOpenPrices = companyHistory.openPrices,
            historyHighPrices = companyHistory.highPrices,
            historyLowPrices = companyHistory.lowPrices,
            historyClosePrices = companyHistory.closePrices,
            historyDatePrices = companyHistory.datePrices,

            newsDates = companyNews.dates,
            newsHeadlines = companyNews.headlines,
            newsIds = companyNews.ids,
            newsPreviewImagesUrls = companyNews.previewImagesUrls,
            newsSources = companyNews.sources,
            newsSummaries = companyNews.summaries,
            newsUrls = companyNews.browserUrls,

            isFavourite = isFavourite,
            favouriteOrderIndex = favouriteOrderIndex,

            isBought = isBought,
            boughtPrice = boughtPrice
        )
    }
}