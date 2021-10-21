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
        StockPrice(1, "", "", "", "", "", ""),
        StockPrice(2, "", "", "", "", "", ""),
        StockPrice(3, "", "", "", "", "", ""),
        StockPrice(4, "", "", "", "", "", ""),
        StockPrice(relationId, "", "", "", "", "", ""),
        StockPrice(6, "", "", "", "", "", ""),
        StockPrice(7, "", "", "", "", "", ""),
        StockPrice(8, "", "", "", "", "", "")
    )

    val uniqueSearchRequests = listOf(
        SearchRequest(1, "Facebook"),
        SearchRequest(2, "Tesla"),
        SearchRequest(3, "Zoom"),
        SearchRequest(4, "Apple"),
        SearchRequest(5, "Android"),
        SearchRequest(6, "Amazon.com")
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
        News(1, 0, "", "", "", "", "", "", ""),
        News(2, relationId, "", "", "", "", "", "", ""),
        News(3, 0, "", "", "", "", "", "", ""),
        News(4, relationId, "", "", "", "", "", "", ""),
        News(5, 0, "", "", "", "", "", "", ""),
        News(6, relationId, "", "", "", "", "", "", ""),
        News(7, 0, "", "", "", "", "", "", "")
    )

    val pastPrices = listOf(
        PastPrice(1, 0, 0.0, "", "", "", 0.0, "", ""),
        PastPrice(2, relationId, 0.0, "", "", "", 0.0, "", ""),
        PastPrice(3, 0, 0.0, "", "", "", 0.0, "", ""),
        PastPrice(4, relationId, 0.0, "", "", "", 0.0, "", ""),
        PastPrice(5, 0, 0.0, "", "", "", 0.0, "", ""),
        PastPrice(6, relationId, 0.0, "", "", "", 0.0, "", "")
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