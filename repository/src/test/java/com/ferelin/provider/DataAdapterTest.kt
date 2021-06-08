package com.ferelin.provider

import com.ferelin.provider.provider.FakeData
import com.ferelin.repository.responseConverter.DataAdapter
import org.junit.Assert
import org.junit.Before
import org.junit.Test

class DataAdapterTest {

    private lateinit var mAdapter: DataAdapter

    @Before
    fun setUp() {
        mAdapter = DataAdapter()
    }

    @Test
    fun fromLongToDateStr() {
        val time = 1616676000000L
        val result = mAdapter.fromLongToDateStr(time)
        Assert.assertEquals("25 Mar 2021", result)
    }

    @Test
    fun getMonthFromDate() {
        val date = "25 Mar 2021"
        val result = mAdapter.parseMonthFromDate(date)
        Assert.assertEquals("Mar", result)
    }

    @Test
    fun getYearFromDate() {
        val date = "25 Mar 2021"
        val result = mAdapter.parseYearFromDate(date)
        Assert.assertEquals("2021", result)
    }

    @Test
    fun adaptName() {
        val name = "Apple Inc"
        val result = mAdapter.adaptName(name)
        Assert.assertEquals("Apple ", result)
    }

    @Test
    fun adaptPhone() {
        val phone = "123456789.0"
        val result = mAdapter.adaptPhone(phone)
        Assert.assertEquals("123456789", result)
    }

    @Test
    fun formatPrice() {
        val price = 1042.32
        val result = mAdapter.formatPrice(price)
        Assert.assertEquals("$1 042.32", result)
    }

    @Test
    fun convertMillisFromResponse() {
        val time = 1234567L
        val result = mAdapter.convertMillisFromResponse(time)
        Assert.assertEquals(1234567000L, result)
    }

    @Test
    fun toDatabaseCompany() {
        val company = FakeData.adaptiveCompany
        mAdapter.toDatabaseCompany(company).also {
            Assert.assertEquals(company.id, it.id)
            Assert.assertEquals(company.companyProfile.name, it.name)
            Assert.assertEquals(company.companyProfile.symbol, it.symbol)
            Assert.assertEquals(company.companyProfile.logoUrl, it.logoUrl)
            Assert.assertEquals(company.companyProfile.country, it.country)
            Assert.assertEquals(company.companyProfile.phone, it.phone)
            Assert.assertEquals(company.companyProfile.webUrl, it.webUrl)
            Assert.assertEquals(company.companyProfile.industry, it.industry)
            Assert.assertEquals(company.companyProfile.currency, it.currency)
            Assert.assertEquals(company.companyProfile.capitalization, it.capitalization)
            Assert.assertEquals(company.companyDayData.currentPrice, it.dayCurrentPrice)
            Assert.assertEquals(company.companyDayData.previousClosePrice, it.dayPreviousClosePrice)
            Assert.assertEquals(company.companyDayData.openPrice, it.dayOpenPrice)
            Assert.assertEquals(company.companyDayData.highPrice, it.dayHighPrice)
            Assert.assertEquals(company.companyDayData.lowPrice, it.dayLowPrice)
            Assert.assertEquals(company.companyDayData.profit, it.dayProfit)
            Assert.assertEquals(company.companyHistory.openPrices, it.historyOpenPrices)
            Assert.assertEquals(company.companyHistory.highPrices, it.historyHighPrices)
            Assert.assertEquals(company.companyHistory.lowPrices, it.historyLowPrices)
            Assert.assertEquals(company.companyHistory.closePrices, it.historyClosePrices)
            Assert.assertEquals(company.companyHistory.datePrices, it.historyDatePrices)
            Assert.assertEquals(company.companyNews.dates, it.newsDates)
            Assert.assertEquals(company.companyNews.headlines, it.newsHeadlines)
            Assert.assertEquals(company.companyNews.ids, it.newsIds)
            Assert.assertEquals(company.companyNews.previewImagesUrls, it.newsPreviewImagesUrls)
            Assert.assertEquals(company.companyNews.sources, it.newsSources)
            Assert.assertEquals(company.companyNews.summaries, it.newsSummaries)
            Assert.assertEquals(company.companyNews.browserUrls, it.newsUrls)
            Assert.assertEquals(company.isFavourite, it.isFavourite)
            Assert.assertEquals(company.favouriteOrderIndex, it.favouriteOrderIndex)
        }
    }

    @Test
    fun toAdaptiveCompany() {
        val company = FakeData.companiesResponseSuccessFromDatabase.companies.first()
        mAdapter.toAdaptiveCompany(company).also {
            Assert.assertEquals(company.id, it.id)
            Assert.assertEquals(company.name, it.companyProfile.name)
            Assert.assertEquals(company.symbol, it.companyProfile.symbol)
            Assert.assertEquals(company.logoUrl, it.companyProfile.logoUrl)
            Assert.assertEquals(company.country, it.companyProfile.country)
            Assert.assertEquals(company.phone, it.companyProfile.phone)
            Assert.assertEquals(company.webUrl, it.companyProfile.webUrl)
            Assert.assertEquals(company.industry, it.companyProfile.industry)
            Assert.assertEquals(company.currency, it.companyProfile.currency)
            Assert.assertEquals(company.capitalization, it.companyProfile.capitalization)
            Assert.assertEquals(company.dayCurrentPrice, it.companyDayData.currentPrice)
            Assert.assertEquals(company.dayPreviousClosePrice, it.companyDayData.previousClosePrice)
            Assert.assertEquals(company.dayOpenPrice, it.companyDayData.openPrice)
            Assert.assertEquals(company.dayHighPrice, it.companyDayData.highPrice)
            Assert.assertEquals(company.dayLowPrice, it.companyDayData.lowPrice)
            Assert.assertEquals(company.dayProfit, it.companyDayData.profit)
            Assert.assertEquals(company.historyOpenPrices, it.companyHistory.openPrices)
            Assert.assertEquals(company.historyHighPrices, it.companyHistory.highPrices)
            Assert.assertEquals(company.historyLowPrices, it.companyHistory.lowPrices)
            Assert.assertEquals(company.historyClosePrices, it.companyHistory.closePrices)
            Assert.assertEquals(company.historyDatePrices, it.companyHistory.datePrices)
            Assert.assertEquals(company.newsDates, it.companyNews.dates)
            Assert.assertEquals(company.newsHeadlines, it.companyNews.headlines)
            Assert.assertEquals(company.newsIds, it.companyNews.ids)
            Assert.assertEquals(company.newsPreviewImagesUrls, it.companyNews.previewImagesUrls)
            Assert.assertEquals(company.newsSources, it.companyNews.sources)
            Assert.assertEquals(company.newsSummaries, it.companyNews.summaries)
            Assert.assertEquals(company.newsUrls, it.companyNews.browserUrls)
            Assert.assertEquals(company.isFavourite, it.isFavourite)
            Assert.assertEquals(company.favouriteOrderIndex, it.favouriteOrderIndex)
        }
    }

    @Test
    fun calculateProfit() {
        val currentPrice = 100.0
        val previousPrice = 50.0
        val result = mAdapter.buildProfitString(currentPrice, previousPrice)
        Assert.assertEquals("+$50.0 (50,0%)" , result)
    }
}