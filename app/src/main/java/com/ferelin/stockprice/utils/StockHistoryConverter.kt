package com.ferelin.stockprice.utils

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

import com.ferelin.repository.adaptiveModels.AdaptiveCompanyHistory
import com.ferelin.repository.adaptiveModels.AdaptiveCompanyHistoryForChart
import com.ferelin.shared.formatPrice
import com.ferelin.shared.parseMonthFromDate
import com.ferelin.shared.parseYearFromDate

/**
 * [StockHistoryConverter] is used to convert history for UI with
 *  special view mode(OneYearMode,SixMonths, etc.)
 */

object StockHistoryConverter {

    fun toCompanyHistoryForChart(history: AdaptiveCompanyHistory): AdaptiveCompanyHistoryForChart {
        return AdaptiveCompanyHistoryForChart(
            price = history.closePrices.map { parsePrice(it) ?: 0.0 },
            priceStr = history.closePrices,
            dates = history.datePrices
        )
    }

    fun toOneYear(history: AdaptiveCompanyHistoryForChart): AdaptiveCompanyHistoryForChart {
        val startMonth = parseMonthFromDate(history.dates.firstOrNull() ?: "")
        val startYear = parseYearFromDate(history.dates.firstOrNull() ?: "")
        val startPrice = history.price.firstOrNull() ?: 0.0
        val startPriceStr = formatPrice(startPrice)
        val startDate = "$startMonth $startYear"

        val endMonth = parseMonthFromDate(history.dates.lastOrNull() ?: "")
        val endYear = parseYearFromDate(history.dates.lastOrNull() ?: "")
        val endPrice = history.price.lastOrNull() ?: 0.0
        val endPriceStr = formatPrice(endPrice)
        val endDate = "$endMonth $endYear"

        return AdaptiveCompanyHistoryForChart(
            listOf(startPrice, endPrice),
            listOf(startPriceStr, endPriceStr),
            listOf(startDate, endDate)
        )
    }

    fun toSixMonths(history: AdaptiveCompanyHistoryForChart): AdaptiveCompanyHistoryForChart {
        val resultsDouble = mutableListOf<Double>()
        val resultsStr = mutableListOf<String>()
        val resultsDates = mutableListOf<String>()
        var amount = 0.0

        history.price.forEachIndexed { index, price ->
            amount += price
            if (index == history.price.size / 2) {
                val average = amount / index
                val from = parseMonthFromDate(history.dates.firstOrNull() ?: "")
                val to = parseMonthFromDate(history.dates.getOrNull(index) ?: "")
                resultsDouble.add(average)
                resultsStr.add(formatPrice(average))
                resultsDates.add("$from - $to")
                amount = 0.0
            }
        }

        val average = amount / history.price.size / 2
        val from =
            parseMonthFromDate(history.dates.getOrNull(history.price.size / 2 + 1) ?: "")
        val to = parseMonthFromDate(history.dates.lastOrNull() ?: "")
        resultsDouble.add(average)
        resultsStr.add(formatPrice(average))
        resultsDates.add("$from - $to")

        return AdaptiveCompanyHistoryForChart(
            resultsDouble.reversed(),
            resultsStr.reversed(),
            resultsDates
        )
    }

    fun toMonths(history: AdaptiveCompanyHistoryForChart): AdaptiveCompanyHistoryForChart {
        val resultsDouble = mutableListOf<Double>()
        val resultsStr = mutableListOf<String>()
        val resultsDates = mutableListOf<String>()
        var currentMonth = parseMonthFromDate(history.dates.firstOrNull() ?: "")
        var cursor = 0

        var amount = 0.0
        history.price.forEachIndexed { index, price ->
            amount += price
            cursor++
            if (currentMonth != parseMonthFromDate(history.dates.getOrNull(index) ?: "")) {
                val average = amount / cursor
                resultsDouble.add(average)
                resultsStr.add(formatPrice(average))
                resultsDates.add(currentMonth)

                currentMonth = parseMonthFromDate(history.dates.getOrNull(index) ?: "")
                cursor = 0
                amount = 0.0
            }
        }

        return AdaptiveCompanyHistoryForChart(
            resultsDouble,
            resultsStr,
            resultsDates
        )
    }

    fun toWeeks(history: AdaptiveCompanyHistoryForChart): AdaptiveCompanyHistoryForChart {
        val resultsDouble = mutableListOf<Double>()
        val resultsStr = mutableListOf<String>()
        val resultsDates = mutableListOf<String>()

        var cursor = 0
        var counter = 0

        var amount = 0.0
        while (cursor < history.price.size - 1) {
            val closePrice = history.price[cursor]
            amount += closePrice
            cursor++
            counter++

            if (counter == 7) {
                resultsDouble.add(closePrice)
                resultsStr.add(history.priceStr.getOrNull(cursor) ?: "")
                resultsDates.add(history.dates.getOrNull(cursor) ?: "")
                counter = 0
            }
        }

        return AdaptiveCompanyHistoryForChart(
            resultsDouble,
            resultsStr,
            resultsDates
        )
    }

    private fun parsePrice(str: String): Double? {
        return str.filter { it.isDigit() || it == '.' }.toDoubleOrNull()
    }
}