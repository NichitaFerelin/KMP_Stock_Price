package com.ferelin.provider

import com.ferelin.local.models.Company
import com.ferelin.local.responses.CompaniesResponse
import com.ferelin.local.responses.Responses

/*
* Mocked data for unit tests
* */
object FakeLocalResponses {

    val company = Company(
        id = 1,
        name = "Apple",
        symbol = "AAPL",
        logoUrl = "https://finnhub.io/api/logo?symbol=AAPL",
        country = "US",
        phone = "14089961010",
        webUrl = "https://www.apple.com/",
        industry = "Technology",
        currency = "USD",
        capitalization = "2272605"
    )

    val companiesResponseSuccessFromJson = CompaniesResponse.Success(
        code = Responses.LOADED_FROM_JSON,
        companies = listOf(
            Company(
                id = 1,
                name = "Apple",
                symbol = "AAPL",
                logoUrl = "https://finnhub.io/api/logo?symbol=AAPL",
                country = "US",
                phone = "14089961010",
                webUrl = "https://www.apple.com/",
                industry = "Technology",
                currency = "USD",
                capitalization = "2272605"
            )
        )
    )
    val companiesResponseSuccessFromDatabase = CompaniesResponse.Success(
        code = Responses.LOADED_FROM_DB,
        companies = listOf(
            Company(
                id = 1,

                name = "Microsoft Corp",

                symbol = "MSFT",
                logoUrl = "https://finnhub.io/api/logo?symbol=MSFT",
                country = "US",
                phone = "14258828080",
                webUrl = "https://www.microsoft.com/en-us",
                industry = "Technology",
                currency = "USD",
                capitalization = "1847768",

                dayCurrentPrice = "$104.3",
                dayPreviousClosePrice = "$99.9",
                dayOpenPrice = "$92.3",
                dayHighPrice = "$105.4",
                dayLowPrice = "$87.4",
                dayProfit = "$2.3",

                historyOpenPrices = listOf("$55.4", "$24.3"),
                historyHighPrices = listOf("$91.2", "$55.4"),
                historyLowPrices = listOf("$33.1", "$99.9"),
                historyClosePrices = listOf("$42.2", "$104.4"),
                historyDatePrices = listOf("$98.8", "$55.3"),

                newsDates = listOf("24 dec 2019", "26 dec 2020"),
                newsHeadlines = listOf("Headline_1", "Headline_2"),
                newsIds = listOf("ID_1001", "ID_1002"),
                newsPreviewImagesUrls = listOf("URL_image_1", "URL_image_2"),
                newsSources = listOf("source_1", "source_2"),
                newsSummaries = listOf("summary_1", "summary_2"),
                newsUrls = listOf("url_1", "url_2"),

                isFavourite = true,
                favouriteOrderIndex = 2
            )
        )
    )
}