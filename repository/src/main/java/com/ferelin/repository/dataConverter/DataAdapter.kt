package com.ferelin.repository.dataConverter

import com.ferelin.local.models.Company
import com.ferelin.repository.adaptiveModels.*
import java.text.SimpleDateFormat
import java.util.*

class DataAdapter {

    fun fromLongToDateStr(time: Long): String {
        val datePattern = "dd MMM yyyy"
        val dateFormat = SimpleDateFormat(datePattern, Locale.ENGLISH)
        return dateFormat.format(Date(time)).filter { it != ',' }
    }

    fun getMonthFromDate(date: String): String {
        return date.filter { it.isLetter() }
    }

    fun adaptName(name: String): String {
        val postfix = name.split(" ").last()
        return if (postfix == "Inc") name.substringBefore("Inc") else name
    }

    fun adaptPhone(phone: String): String {
        return phone.substringBefore('.')
    }

    fun formatPrice(price: Double): String {
        return "$${adaptPrice(price)}"
    }

    fun convertMillisFromResponse(time: Long): Long {
        val timeStr = time.toString()
        val resultStr = "${timeStr}000"
        return resultStr.toLong()
    }

    fun toDatabaseCompany(adaptiveCompany: AdaptiveCompany): Company {
        return Company(
            id = adaptiveCompany.id,

            name = adaptiveCompany.companyProfile.name,

            symbol = adaptiveCompany.companyProfile.symbol,
            logoUrl = adaptiveCompany.companyProfile.logoUrl,
            country = adaptiveCompany.companyProfile.country,
            phone = adaptiveCompany.companyProfile.phone,
            webUrl = adaptiveCompany.companyProfile.webUrl,
            industry = adaptiveCompany.companyProfile.industry,
            currency = adaptiveCompany.companyProfile.currency,
            capitalization = adaptiveCompany.companyProfile.capitalization,

            dayCurrentPrice = adaptiveCompany.companyDayData.currentPrice,
            dayPreviousClosePrice = adaptiveCompany.companyDayData.previousClosePrice,
            dayOpenPrice = adaptiveCompany.companyDayData.openPrice,
            dayHighPrice = adaptiveCompany.companyDayData.highPrice,
            dayLowPrice = adaptiveCompany.companyDayData.lowPrice,
            dayProfit = adaptiveCompany.companyDayData.profit,

            historyOpenPrices = adaptiveCompany.companyHistory.openPrices,
            historyHighPrices = adaptiveCompany.companyHistory.highPrices,
            historyLowPrices = adaptiveCompany.companyHistory.lowPrices,
            historyClosePrices = adaptiveCompany.companyHistory.closePrices,
            historyDatePrices = adaptiveCompany.companyHistory.datePrices,

            newsDates = adaptiveCompany.companyNews.dates,
            newsHeadlines = adaptiveCompany.companyNews.headlines,
            newsIds = adaptiveCompany.companyNews.ids,
            newsPreviewImagesUrls = adaptiveCompany.companyNews.previewImagesUrls,
            newsSources = adaptiveCompany.companyNews.sources,
            newsSummaries = adaptiveCompany.companyNews.summaries,
            newsUrls = adaptiveCompany.companyNews.browserUrls,

            isFavourite = adaptiveCompany.isFavourite
        )
    }

    fun toAdaptiveCompany(company: Company): AdaptiveCompany {
        return AdaptiveCompany(
            id = company.id,
            companyProfile = AdaptiveCompanyProfile(
                name = company.name,
                symbol = company.symbol,
                logoUrl = company.logoUrl,
                country = company.country,
                phone = company.phone,
                webUrl = company.webUrl,
                industry = company.industry,
                currency = company.currency,
                capitalization = company.capitalization
            ),
            companyDayData = AdaptiveCompanyDayData(
                currentPrice = company.dayCurrentPrice,
                previousClosePrice = company.dayPreviousClosePrice,
                openPrice = company.dayOpenPrice,
                highPrice = company.dayHighPrice,
                lowPrice = company.dayLowPrice,
                profit = company.dayProfit
            ),
            companyHistory = AdaptiveCompanyHistory(
                openPrices = company.historyOpenPrices,
                highPrices = company.historyHighPrices,
                lowPrices = company.historyLowPrices,
                closePrices = company.historyClosePrices,
                datePrices = company.historyDatePrices
            ),
            companyNews = AdaptiveCompanyNews(
                dates = company.newsDates,
                headlines = company.newsHeadlines,
                ids = company.newsIds,
                previewImagesUrls = company.newsPreviewImagesUrls,
                sources = company.newsSources,
                summaries = company.newsSummaries,
                browserUrls = company.newsUrls
            ),
            companyStyle = AdaptiveCompanyStyle(),
            isFavourite = company.isFavourite
        )
    }

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

    fun toAdaptiveCompanyFromJson(company: Company): AdaptiveCompany {
        return toAdaptiveCompany(company).also {
            it.companyProfile.capitalization =
                adaptPrice(it.companyProfile.capitalization.toDouble())
        }
    }

    private fun adaptPrice(price: Double): String {
        var resultStr = ""
        val priceStr = price.toString()

        val reminder = priceStr.substringAfter(".")
        var formattedSeparator = "."
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