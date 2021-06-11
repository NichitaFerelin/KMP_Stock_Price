package com.ferelin

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.ferelin.local.database.CompaniesDao
import com.ferelin.local.database.CompaniesDatabase
import com.ferelin.provider.FakeLocalResponses
import kotlinx.coroutines.asExecutor
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class CompaniesDatabaseTest {

    private lateinit var mDatabase: CompaniesDatabase
    private lateinit var mDatabaseDao: CompaniesDao

    private val mTestDispatcher = TestCoroutineDispatcher()

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        mDatabase = Room.inMemoryDatabaseBuilder(context, CompaniesDatabase::class.java)
            .setTransactionExecutor(mTestDispatcher.asExecutor())
            .setQueryExecutor(mTestDispatcher.asExecutor())
            .allowMainThreadQueries()
            .build()
        mDatabaseDao = mDatabase.companiesDao()
    }

    @Test
    fun insert(): Unit = runBlocking {
        val company = FakeLocalResponses.companiesResponseSuccessFromDatabase.companies.first()
        mDatabaseDao.insert(company)
        mDatabaseDao.getAll().first().also {
            Assert.assertEquals(company.id, it.id)
            Assert.assertEquals(company.name, it.name)
            Assert.assertEquals(company.symbol, it.symbol)
            Assert.assertEquals(company.logoUrl, it.logoUrl)
            Assert.assertEquals(company.country, it.country)
            Assert.assertEquals(company.phone, it.phone)
            Assert.assertEquals(company.webUrl, it.webUrl)
            Assert.assertEquals(company.industry, it.industry)
            Assert.assertEquals(company.currency, it.currency)
            Assert.assertEquals(company.capitalization, it.capitalization)
            Assert.assertEquals(company.dayCurrentPrice, it.dayCurrentPrice)
            Assert.assertEquals(company.dayPreviousClosePrice, it.dayPreviousClosePrice)
            Assert.assertEquals(company.dayOpenPrice, it.dayOpenPrice)
            Assert.assertEquals(company.dayHighPrice, it.dayHighPrice)
            Assert.assertEquals(company.dayLowPrice, it.dayLowPrice)
            Assert.assertEquals(company.dayProfit, it.dayProfit)
            Assert.assertEquals(company.historyOpenPrices, it.historyOpenPrices)
            Assert.assertEquals(company.historyHighPrices, it.historyHighPrices)
            Assert.assertEquals(company.historyLowPrices, it.historyLowPrices)
            Assert.assertEquals(company.historyClosePrices, it.historyClosePrices)
            Assert.assertEquals(company.historyDatePrices, it.historyDatePrices)
            Assert.assertEquals(company.newsDates, it.newsDates)
            Assert.assertEquals(company.newsHeadlines, it.newsHeadlines)
            Assert.assertEquals(company.newsIds, it.newsIds)
            Assert.assertEquals(company.newsPreviewImagesUrls, it.newsPreviewImagesUrls)
            Assert.assertEquals(company.newsSources, it.newsSources)
            Assert.assertEquals(company.newsSummaries, it.newsSummaries)
            Assert.assertEquals(company.newsUrls, it.newsUrls)
            Assert.assertEquals(company.isFavourite, it.isFavourite)
            Assert.assertEquals(company.favouriteOrderIndex, it.favouriteOrderIndex)
        }
    }

    @Test
    fun insertAll(): Unit = runBlocking {
        val firstCompany = FakeLocalResponses.companiesResponseSuccessFromDatabase.companies.first()
        val secondCompany = firstCompany.copy(id = 10, name = "newName")
        val companies = listOf(firstCompany, secondCompany)
        mDatabaseDao.insertAll(companies)
        mDatabaseDao.getAll().also {
            Assert.assertEquals(2, it.size)
        }
    }

    @Test
    fun update(): Unit = runBlocking {
        val company = FakeLocalResponses.companiesResponseSuccessFromDatabase.companies.first()
        val updated = company.copy(name = "newName")
        mDatabaseDao.insert(company)
        mDatabaseDao.update(updated)
        mDatabaseDao.getAll().first().also {
            Assert.assertEquals(it.name, updated.name)
        }
    }

    @After
    fun close() {
        mDatabase.close()
    }
}