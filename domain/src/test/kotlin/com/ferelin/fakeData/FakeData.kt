package com.ferelin.fakeData

import com.ferelin.domain.entities.*

object FakeData {

    val relationId = 5
    val defaultSizeByRelationId = 3

    val companies = listOf(
        Company(1, "", "", ""),
        Company(2, "", "", ""),
        Company(3, "", "", ""),
        Company(4, "", "", ""),
        Company(5, "", "", ""),
        Company(6, "", "", ""),
        Company(7, "", "", ""),
        Company(8, "", "", "")
    )

    val favouriteCompaniesCount = 3
    val companiesWithFavourites = listOf(
        Company(1, "", "", ""),
        Company(2, "", "", "", true, 0),
        Company(3, "", "", ""),
        Company(4, "", "", ""),
        Company(5, "", "", "", true, 1),
        Company(6, "", "", ""),
        Company(7, "", "", "", true, 2),
        Company(8, "", "", "")
    )

    val stockPrices = listOf(
        StockPrice(1, 0.0, 0.0, 0.0, 0.0, 0.0),
        StockPrice(2, 0.0, 0.0, 0.0, 0.0, 0.0),
        StockPrice(3, 0.0, 0.0, 0.0, 0.0, 0.0),
        StockPrice(4, 0.0, 0.0, 0.0, 0.0, 0.0),
        StockPrice(relationId, 0.0, 0.0, 0.0, 0.0, 0.0),
        StockPrice(6, 0.0, 0.0, 0.0, 0.0, 0.0),
        StockPrice(7, 0.0, 0.0, 0.0, 0.0, 0.0),
        StockPrice(8, 0.0, 0.0, 0.0, 0.0, 0.0)
    )

    val uniqueSearchRequests = setOf(
        "Facebook",
        "Tesla",
        "Zoom",
        "Apple",
        "Android",
        "Amazon.com"
    )

    val duplicatedStrings = listOf(
        "Face",
        "Faceb",
        "Faceboo",
        "Android",
        "Facebook",
        "Zoom",
        "Tesla"
    )

    val reorderingStrings = listOf(
        "Tesla",
        "Zoom",
        "Facebook",
        "Android"
    )

    val news = listOf(
        News(1, 0, "", "", "", "", "", "", 0L),
        News(2, relationId, "", "", "", "", "", "", 0L),
        News(3, 0, "", "", "", "", "", "", 0L),
        News(4, relationId, "", "", "", "", "", "", 0L),
        News(5, 0, "", "", "", "", "", "", 0L),
        News(6, relationId, "", "", "", "", "", "", 0L),
        News(7, 0, "", "", "", "", "", "", 0L)
    )

    val pastPrices = listOf(
        PastPrice(1, 0, 0.0, 0.0, 0.0, 0.0, 0L),
        PastPrice(2, relationId, 0.0, 0.0, 0.0, 0.0, 0L),
        PastPrice(3, 0, 0.0, 0.0, 0.0, 0.0, 0L),
        PastPrice(4, relationId, 0.0, 0.0, 0.0, 0.0, 0L),
        PastPrice(5, 0, 0.0, 0.0, 0.0, 0.0, 0L),
        PastPrice(6, relationId, 0.0, 0.0, 0.0, 0.0, 0L)
    )

    val profiles = listOf(
        Profile(1, "", "", "", "", "", ""),
        Profile(2, "", "", "", "", "", ""),
        Profile(3, "", "", "", "", "", ""),
        Profile(4, "", "", "", "", "", ""),
        Profile(relationId, "", "", "", "", "", ""),
        Profile(7, "", "", "", "", "", ""),
        Profile(8, "", "", "", "", "", ""),
        Profile(9, "", "", "", "", "", ""),
    )
}