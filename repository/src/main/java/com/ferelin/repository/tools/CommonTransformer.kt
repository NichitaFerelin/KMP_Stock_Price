package com.ferelin.repository.tools

import com.ferelin.local.model.Company
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.utilits.TimeMillis
import java.text.DateFormat
import java.util.*

object CommonTransformer {

    fun fromLongToDateStr(time: Long): String {
        val convertedTime = TimeMillis.convertFromResponse(time)
        val locale = Locale("en", "EN")
        val dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale)
        return dateFormat.format(Date(convertedTime))
    }

    fun transformCompanyName(name: String): String {
        val postfix = name.split(" ").last()
        return if (postfix == "Inc") name.substringBefore("Inc") else name
    }

    fun transformPhone(phone: String): String {
        return phone.substringBefore('.')
    }

    fun transformPriceStr(priceStr: String, separator: Char = '.'): String {
        var resultStr = ""
        if (priceStr.last() == '0' && priceStr[priceStr.lastIndex - 1] == '.') {
            resultStr = priceStr.substring(0, priceStr.lastIndex - 1)
        } else {
            var cursor = priceStr.lastIndex
            var digitCounter = 0
            while (cursor >= 0) {
                val currentSymbol = priceStr[cursor]
                when {
                    currentSymbol.isDigit() -> {
                        resultStr += currentSymbol
                        digitCounter++
                        if (digitCounter == 3 && cursor != 0) {
                            resultStr += " "
                            digitCounter = 0
                        }
                    }
                    else -> {
                        resultStr += separator
                        resultStr.trim()
                        digitCounter = 0
                    }
                }
                cursor--
            }
        }
        return resultStr
    }

    fun transformPriceStr(priceStr: String, currency: String): String {
        val currencySymbol = when (currency) {
            "RUB" -> "₽"
            "USD" -> "$"
            else -> "?"
        }

        val separatorSymbol = when (currencySymbol) {
            "₽" -> ','
            else -> '.'
        }

        val resultStr = transformPriceStr(priceStr, separatorSymbol)
        return when (currencySymbol) {
            "$" -> "/$${resultStr.reversed()}"
            else -> "${resultStr.reversed()} ?"
        }
    }

    fun calculatePercentDayProfitList(
        openPrices: List<Double>,
        closePrices: List<Double>
    ): List<String> {
        return List(openPrices.size) {
            val openPrice = openPrices[it]
            val closePrice = closePrices[it]
            val numberProfit = (closePrice - openPrice).toString()
            val digitNumberProfit = numberProfit.substringBefore('.').filter { it.isDigit() }
            val remainderNumberProfit = numberProfit.substringBefore('.').substring(0, 2)

            val percentProfit = (100 * (closePrice - openPrice) / openPrice).toString()
            val digitPercentProfit = percentProfit.substringBefore('.').filter { it.isDigit() }
            val remainderPercentProfit = percentProfit.substringBefore('.').substring(0, 2)

            val prefix = if (closePrice > openPrice) "+" else "-"
            "$prefix/$$digitNumberProfit.$remainderNumberProfit ($digitPercentProfit,$remainderPercentProfit%)"
        }
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
            adaptiveCompany.lastPrice,
            adaptiveCompany.openPrices,
            adaptiveCompany.highPrices,
            adaptiveCompany.lowPrices,
            adaptiveCompany.closePrices,
            adaptiveCompany.timestamps
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
            company.lastPrice,
            company.isFavourite,
            company.openPrices,
            company.highPrices,
            company.lowPrices,
            company.closePrices,
            timestamps = company.timestamps,
        )
    }

    // For Json load
    fun toAdaptiveCompanyWithTransform(company: Company): AdaptiveCompany {
        return AdaptiveCompany(
            transformCompanyName(company.name),
            company.symbol,
            company.ticker,
            company.logoUrl,
            company.country,
            transformPhone(company.phone),
            company.webUrl,
            company.industry,
            company.currency,
            transformPriceStr(company.capitalization, "USD"),
            transformPriceStr(company.lastPrice, company.currency),
            company.isFavourite
        )
    }
}