package com.ferelin.repository.utils

import com.ferelin.repository.adaptiveModels.AdaptiveCompanyHistory
import com.ferelin.repository.adaptiveModels.AdaptiveCompanyHistoryForChart
import com.ferelin.repository.dataConverter.DataAdapter

object StockHistoryConverter {

    private val mAdapter = DataAdapter()

    private fun parsePrice(str: String): Double? {
        return str.filter { it.isDigit() || it == '.' }.toDoubleOrNull()
    }

    fun toCompanyHistoryForChart(history: AdaptiveCompanyHistory): AdaptiveCompanyHistoryForChart {
        return AdaptiveCompanyHistoryForChart(
            price = history.closePrices.map { parsePrice(it) ?: 0.0 },
            priceStr = history.closePrices,
            dates = history.datePrices
        )
    }

    fun toOneYear(history: AdaptiveCompanyHistoryForChart): AdaptiveCompanyHistoryForChart {
        var amount = 0.0
        history.price.forEach { amount += it }

        val from = mAdapter.getMonthFromDate(history.dates[0])
        val to = mAdapter.getMonthFromDate(history.dates.last())
        val dateStr = "$from - $to"
        val average = amount / history.price.size
        val averageStr = mAdapter.formatPrice(average)

        return AdaptiveCompanyHistoryForChart(
            listOf(average),
            listOf(averageStr),
            listOf(dateStr)
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
                val from = mAdapter.getMonthFromDate(history.dates[0])
                val to = mAdapter.getMonthFromDate(history.dates[index])
                resultsDouble.add(average)
                resultsStr.add(mAdapter.formatPrice(average))
                resultsDates.add("$from - $to")
                amount = 0.0
            }
        }

        val average = amount / history.price.size / 2
        val from = mAdapter.getMonthFromDate(history.dates[history.price.size / 2 + 1])
        val to = mAdapter.getMonthFromDate(history.dates.last())
        resultsDouble.add(average)
        resultsStr.add(mAdapter.formatPrice(average))
        resultsDates.add("$from - $to")

        return AdaptiveCompanyHistoryForChart(
            resultsDouble,
            resultsStr,
            resultsDates
        )
    }

    fun toMonths(history: AdaptiveCompanyHistoryForChart): AdaptiveCompanyHistoryForChart {
        val resultsDouble = mutableListOf<Double>()
        val resultsStr = mutableListOf<String>()
        val resultsDates = mutableListOf<String>()
        var currentMonth = mAdapter.getMonthFromDate(history.dates.first())
        var cursor = 0

        var amount = 0.0
        history.price.forEachIndexed { index, price ->
            amount += price
            cursor++
            if (currentMonth != mAdapter.getMonthFromDate(history.dates[index])) {
                val average = amount / cursor
                resultsDouble.add(average)
                resultsStr.add(mAdapter.formatPrice(average))
                resultsDates.add(currentMonth)

                currentMonth = mAdapter.getMonthFromDate(history.dates[index])
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
                resultsStr.add(history.priceStr[cursor])
                resultsDates.add(history.dates[cursor])
                counter = 0
            }
        }

        return AdaptiveCompanyHistoryForChart(
            resultsDouble,
            resultsStr,
            resultsDates
        )
    }

    fun toDay(history: AdaptiveCompanyHistoryForChart): AdaptiveCompanyHistoryForChart {
        return toOneYear(history)
    }
}