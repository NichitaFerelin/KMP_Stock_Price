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

package com.ferelin.feature_chart.mapper

import com.ferelin.domain.entities.PastPrice
import com.ferelin.feature_chart.utils.extractPrice
import com.ferelin.feature_chart.utils.parseMonthFromDate
import com.ferelin.feature_chart.utils.parseYearFromDate
import com.ferelin.feature_chart.utils.sum
import com.ferelin.feature_chart.viewData.ChartPastPrice
import com.ferelin.shared.toStrPrice

/**
 * [PastPriceTypeMapper] is used to convert history for UI with
 *  special view mode(OneYearMode,SixMonths, etc.)
 */
class PastPriceTypeMapper {

    fun mapForChart(pastPrice: List<PastPrice>): ChartPastPrice {
        val prices = List(pastPrice.size) { extractPrice(pastPrice[it].closePriceStr) ?: 0.0 }
        val pricesStr = List(pastPrice.size) { pastPrice[it].closePriceStr }
        val dates = List(pastPrice.size) { pastPrice[it].date }
        return ChartPastPrice(prices, pricesStr, dates)
    }

    fun mapToYear(pastPrices: List<PastPrice>): ChartPastPrice? {
        if (pastPrices.size < 2) {
            return null
        }

        val startMonth = parseMonthFromDate(pastPrices[0].date)
        val startYear = parseYearFromDate(pastPrices[0].date)
        val startDate = "$startMonth $startYear"

        val endMonth = parseMonthFromDate(pastPrices[1].date)
        val endYear = parseMonthFromDate(pastPrices[1].date)
        val endDate = "$endMonth $endYear"

        return ChartPastPrice(
            prices = listOf(pastPrices[0].closePrice, pastPrices[1].closePrice),
            pricesStr = listOf(pastPrices[0].closePriceStr, pastPrices[1].closePriceStr),
            dates = listOf(startDate, endDate)
        )
    }

    fun mapToHalfYear(pastPrices: List<PastPrice>): ChartPastPrice? {
        if (pastPrices.isEmpty()) {
            return null
        }

        val firstHalfBorder = pastPrices.size / 2
        val firstHalfAverage = pastPrices.sum(0, firstHalfBorder) / (firstHalfBorder + 1)
        val firstHalfFrom = parseMonthFromDate(pastPrices[0].date)
        val firstHalfTo = parseMonthFromDate(pastPrices[firstHalfBorder].date)

        val secondHalfAmount = pastPrices.sum(firstHalfBorder + 1, pastPrices.lastIndex)
        val secondHalfAverage = secondHalfAmount / pastPrices.size - firstHalfBorder + 1
        val secondHalfFrom = parseMonthFromDate(pastPrices[firstHalfBorder + 1].date)
        val secondHalfTo = parseMonthFromDate(pastPrices[pastPrices.lastIndex].date)

        return ChartPastPrice(
            prices = listOf(firstHalfAverage, secondHalfAverage),
            pricesStr = listOf(firstHalfAverage.toStrPrice(), secondHalfAverage.toStrPrice()),
            dates = listOf("$firstHalfFrom - $firstHalfTo", "$secondHalfFrom - $secondHalfTo")
        )
    }

    fun mapToMonths(pastPrices: List<PastPrice>): ChartPastPrice? {
        if (pastPrices.isEmpty()) {
            return null
        }

        val prices = mutableListOf<Double>()
        val pricesStr = mutableListOf<String>()
        val dates = mutableListOf<String>()

        var stepMonth = parseMonthFromDate(pastPrices[0].date)
        var pastPricesCounter = 0
        var stepAmount = 0.0

        pastPrices.forEach { pastPrice ->
            stepAmount += pastPrice.closePrice
            pastPricesCounter++

            val currentMonth = parseMonthFromDate(pastPrice.date)

            if (stepMonth != currentMonth) {
                val average = stepAmount / pastPricesCounter

                prices.add(average)
                pricesStr.add(average.toStrPrice())
                dates.add(stepMonth)

                stepMonth = currentMonth
                pastPricesCounter = 0
                stepAmount = 0.0
            }
        }
        return ChartPastPrice(prices, pricesStr, dates)
    }

    fun mapToWeeks(pastPrices: List<PastPrice>): ChartPastPrice? {
        if (pastPrices.isEmpty()) {
            return null
        }

        val prices = mutableListOf<Double>()
        val pricesStr = mutableListOf<String>()
        val dates = mutableListOf<String>()

        var pastPricesCounter = 0
        var stepAmount = 0.0
        val daysInWeek = 7

        pastPrices.forEach { pastPrice ->
            stepAmount += pastPrice.closePrice
            pastPricesCounter++

            if (pastPricesCounter == daysInWeek) {
                val average = stepAmount / daysInWeek

                prices.add(average)
                pricesStr.add(average.toStrPrice())
                dates.add(pastPrice.date)

                stepAmount = 0.0
                pastPricesCounter = 0
            }
        }
        return ChartPastPrice(prices, pricesStr, dates)
    }
}