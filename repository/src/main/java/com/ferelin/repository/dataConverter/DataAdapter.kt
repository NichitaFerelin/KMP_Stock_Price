package com.ferelin.repository.dataConverter

import com.ferelin.local.model.Company
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.repository.utilits.Currency
import com.ferelin.repository.utilits.TimeMillis
import java.text.DateFormat
import java.util.*

class DataAdapter {

    fun fromLongToDateStr(time: Long): String {
        val convertedTime = TimeMillis.convertFromResponse(time)
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
            Currency.USD_SYMBOL -> "${currencySymbol}${resultStr.reversed()}"
            Currency.RUB_SYMBOL -> "${resultStr.reversed()} $currencySymbol"
            else -> "?${resultStr.reversed()}"
        }
    }

    fun createDayProfitList(
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
            adaptiveCompany.dayProfitPercents,
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
            dayProfitPercents = company.dayProfit
        )
    }

    fun toAdaptiveCompanyFromJson(company: Company): AdaptiveCompany {
        return toAdaptiveCompany(company).also {
            it.capitalization = adaptPrice(it.capitalization.toDouble(), Currency.USD)
        }
    }

    private fun adaptPrice(price: Double, separator: Char = '.'): String {
        val priceStr = price.toString()
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
}