package com.ferelin.repository.dataConverter

import android.util.Log
import com.ferelin.local.model.Company
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.utilits.Currency
import com.ferelin.repository.utilits.Time
import java.text.DateFormat
import java.util.*

class DataAdapter {

    fun fromLongToDateStr(time: Long): String {
        val convertedTime = Time.convertMillisFromResponse(time)
        val locale = Locale("en", "EN")
        val dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale)
        return dateFormat.format(Date(convertedTime)).filter { it != ',' }
    }

    fun adaptName(name: String): String {
        val postfix = name.split(" ").last()
        return if (postfix == "Inc") name.substringBefore("Inc") else name
    }

    fun adaptPhone(phone: String): String {
        return phone.substringBefore('.')
    }

    fun adaptPrice(price: Double, currency: String = Currency.USD): String {
        val currencySymbol = when (currency) {
            Currency.RUB -> Currency.RUB_SYMBOL
            Currency.USD -> Currency.USD_SYMBOL
            else -> "?"
        }

        val separatorSymbol = when (currencySymbol) {
            Currency.RUB_SYMBOL -> ','
            else -> '.'
        }

        val resultStr = adaptPrice(price, separatorSymbol)
        return when (currencySymbol) {
            Currency.USD_SYMBOL -> "$currencySymbol$resultStr"
            Currency.RUB_SYMBOL -> "$resultStr $currencySymbol"
            else -> "?$resultStr"
        }
    }

    /*fun createDayProfitList(
        openPrices: List<Double>,
        closePrices: List<Double>
    ): List<String> {
        return List(openPrices.size) {
            val openPrice = openPrices[it]
            val closePrice = closePrices[it]
            val numberProfit = closePrice - openPrice
            val numberProfitStr = numberProfit.toString()

            val digitNumberProfit = numberProfitStr.substringBefore('.').filter { it.isDigit() }
            val remainderNumberProfit = with(numberProfitStr.substringAfter('.')) {
                if (length >= 2) substring(0, 2) else this
            }

            val percentProfit = (100 * (closePrice - openPrice) / openPrice).toString()
            val digitPercentProfit = percentProfit.substringBefore('.').filter { it.isDigit() }
            val remainderPercentProfit = with(percentProfit.substringAfter('.')) {
                if (length >= 2) substring(0, 2) else this
            }

            val prefix = if (closePrice > openPrice) "+" else "-"
            "$prefix$$digitNumberProfit.$remainderNumberProfit ($digitPercentProfit,$remainderPercentProfit%)"
        }
    }*/

    fun calculateProfit(currentPrice: Double, previousPrice: Double): String {
        val numberProfit = currentPrice - previousPrice
        val numberProfitStr = numberProfit.toString()

        val digitNumberProfit = numberProfitStr.substringBefore('.').filter { it.isDigit() }
        val remainderNumberProfit = with(numberProfitStr.substringAfter('.')) {
            if (length >= 2) substring(0, 2) else this
        }

        val percentProfit = (100 * (currentPrice - previousPrice) / currentPrice).toString()
        val digitPercentProfit = percentProfit.substringBefore('.').filter { it.isDigit() }
        val remainderPercentProfit = with(percentProfit.substringAfter('.')) {
            if (length >= 2) substring(0, 2) else this
        }

        val prefix = if (currentPrice > previousPrice) "+" else "-"
        return "$prefix$$digitNumberProfit.$remainderNumberProfit ($digitPercentProfit,$remainderPercentProfit%)"
    }

    fun toDatabaseCompany(adaptiveCompany: AdaptiveCompany): Company {
        return Company(
            adaptiveCompany.name,
            adaptiveCompany.symbol,
            adaptiveCompany.ticker,
            adaptiveCompany.logoUrl,
            adaptiveCompany.country,
            adaptiveCompany.phone,
            adaptiveCompany.webUrl,
            adaptiveCompany.industry,
            adaptiveCompany.currency,
            adaptiveCompany.capitalization,
            adaptiveCompany.isFavourite,
            adaptiveCompany.dayCurrentPrice,
            adaptiveCompany.dayPreviousClosePrice,
            adaptiveCompany.dayOpenPrice,
            adaptiveCompany.dayHighPrice,
            adaptiveCompany.dayLowPrice,
            adaptiveCompany.dayProfit,
            adaptiveCompany.historyOpenPrices,
            adaptiveCompany.historyHighPrices,
            adaptiveCompany.historyLowPrices,
            adaptiveCompany.historyClosePrices,
            adaptiveCompany.historyTimestampsPrices,
            adaptiveCompany.newsTimestamps,
            adaptiveCompany.newsHeadline,
            adaptiveCompany.newsIds,
            adaptiveCompany.newsImages,
            adaptiveCompany.newsSource,
            adaptiveCompany.newsSummary,
            adaptiveCompany.newsUrl
        )
    }

    fun toAdaptiveCompany(company: Company): AdaptiveCompany {
        return AdaptiveCompany(
            company.name,
            company.symbol,
            company.ticker,
            company.logoUrl,
            company.country,
            company.phone,
            company.webUrl,
            company.industry,
            company.currency,
            company.capitalization,
            company.dayCurrentPrice,
            company.dayPreviousClosePrice,
            company.dayOpenPrice,
            company.dayHighPrice,
            company.dayLowPrice,
            company.dayProfit,
            company.isFavourite,
            company.historyOpenPrices,
            company.historyHighPrices,
            company.historyLowPrices,
            company.historyClosePrices,
            company.historyTimestampsPrices,
            company.newsTimestamps,
            company.newsHeadline,
            company.newsIds,
            company.newsImages,
            company.newsSource,
            company.newsSummary,
            company.newsUrl
        )
    }

    fun toAdaptiveCompanyFromJson(company: Company): AdaptiveCompany {
        return toAdaptiveCompany(company).also {
            it.capitalization = adaptPrice(it.capitalization.toDouble(), Currency.USD)
        }
    }

    private fun adaptPrice(price: Double, separator: Char = '.'): String {
        Log.d("Test", "[price: ${price.toString()}]")
        var resultStr = ""
        val priceStr = price.toString()

        val reminder = priceStr.substringAfter(".")
        var formattedSeparator = separator.toString()
        val formattedReminder = when {
            reminder.length > 2 -> reminder.substring(0, 2)
            reminder.last() == '0' -> {
                formattedSeparator = ""
                ""
            }
            else -> reminder
        }

        val integer = priceStr.substringBefore(".")
        var counter = 0
        for (index in integer.length - 1 downTo 0) {
            resultStr += integer[index]
            counter++
            if (counter == 3 && index != 0) {
                resultStr += " "
                counter = 0
            }
        }
        return "${resultStr.reversed()}$formattedSeparator$formattedReminder"
    }
}