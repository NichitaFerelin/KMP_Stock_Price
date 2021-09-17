package com.ferelin

import com.ferelin.local.LocalTasks
import com.ferelin.local.LocalManagerImpl
import com.ferelin.local.jsonReader.AppJsonReader
import com.ferelin.local.jsonReader.AppJsonReaderImpl
import com.ferelin.local.dataStorage.DataStorage
import com.ferelin.local.dataStorage.DataStorageImpl
import com.ferelin.local.responses.CompaniesResponse
import com.ferelin.local.responses.Responses
import com.ferelin.provider.FakeLocalResponses
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.Spy

class LocalManagerImplTest {

    private lateinit var mLocalManager: LocalTasks

    @Spy
    private lateinit var mJsonReader: AppJsonReader

    @Spy
    private lateinit var mCompaniesManager: CompaniesManager

    @Spy
    private lateinit var mDataStorage: DataStorage

    @Before
    fun setUp() {
        mJsonReader = mock(AppJsonReaderImpl::class.java)
        mCompaniesManager = mock(CompaniesManagerImpl::class.java)
        mDataStorage = mock(DataStorageImpl::class.java)
        mLocalManager = LocalManagerImpl(mJsonReader, mCompaniesManager, mDataStorage)
    }

    @Test
    fun insertCompany() {
        val item = FakeLocalResponses.companiesResponseSuccessFromDatabase.companies.first()
        mLocalManager.insertCompany(item)
        verify(mCompaniesManager, times(1)).insertCompany(item)
    }

    @Test
    fun insertAllCompanies() {
        val first = FakeLocalResponses.companiesResponseSuccessFromDatabase.companies.first()
        val second = first.copy(id = 5)
        val items = listOf(first, second)
        mLocalManager.insertAllCompanies(items)
        verify(mCompaniesManager, times(1)).insertAllCompanies(items)
    }

    @Test
    fun updateCompany() {
        val item = FakeLocalResponses.companiesResponseSuccessFromDatabase.companies.first()
        mLocalManager.updateCompany(item)
        verify(mCompaniesManager, times(1)).updateCompany(item)
    }

    @Test
    fun getAllCompanies(): Unit = runBlocking {
        `when`(mLocalManager.getCompanies()).thenReturn(emptyList())
        mLocalManager.getCompanies()
        verify(mCompaniesManager, times(1)).getCompanies()
    }

    @Test
    fun responseLoadedFromDatabase(): Unit = runBlocking {
        `when`(mCompaniesManager.getCompanies()).thenReturn(listOf(FakeLocalResponses.company))

        val response = mLocalManager.getAllCompaniesAsResponse()
        Assert.assertEquals(true, response is CompaniesResponse.Success)
        Assert.assertEquals(
            Responses.LOADED_FROM_DB,
            (response as CompaniesResponse.Success).code
        )
    }

    @Test
    fun responseLoadedFromJson(): Unit = runBlocking {
        `when`(mCompaniesManager.getCompanies()).thenReturn(emptyList())
        `when`(mLocalManager.getCompaniesFromJson()).thenReturn(emptyList())

        val response = mLocalManager.getAllCompaniesAsResponse()
        Assert.assertEquals(true, response is CompaniesResponse.Success)
        Assert.assertEquals(
            Responses.LOADED_FROM_JSON,
            (response as CompaniesResponse.Success).code
        )
    }

    @Test
    fun getAllCompaniesAsResponseFirstTime(): Unit = runBlocking {
        val item = listOf(FakeLocalResponses.companiesResponseSuccessFromJson.companies.first())

        `when`(mCompaniesManager.getCompanies()).thenReturn(emptyList())
        `when`(mJsonReader.getCompaniesFromJson()).thenReturn(item)

        mLocalManager.getAllCompaniesAsResponse()
        verify(mCompaniesManager, times(1)).getCompanies()
        verify(mJsonReader, times(1)).getCompaniesFromJson()
        verify(mCompaniesManager, times(1)).insertAllCompanies(item)
    }

    @Test
    fun getAllCompaniesAsResponse(): Unit = runBlocking {
        `when`(mCompaniesManager.getCompanies())
            .thenReturn(listOf(FakeLocalResponses.companiesResponseSuccessFromJson.companies.first()))

        mLocalManager.getAllCompaniesAsResponse()
        verify(mCompaniesManager, times(1)).getCompanies()
        verify(mJsonReader, times(0)).getCompaniesFromJson()
        verify(mCompaniesManager, times(0)).insertAllCompanies(emptyList())
    }

    @Test
    fun getSearchesHistoryAsResponse() : Unit = runBlocking {
        mLocalManager.getSearchesHistoryAsResponse()
        verify(mDataStorage, times(1)).getSearchRequestsHistory()
    }

    @Test
    fun getCompaniesFromJson(): Unit = runBlocking {
        `when`(mLocalManager.getCompaniesFromJson()).thenReturn(emptyList())
        mLocalManager.getCompaniesFromJson()
        verify(mJsonReader, times(1)).getCompaniesFromJson()
    }

    @Test
    fun getSearchesHistory(): Unit = runBlocking {
        `when`(mLocalManager.getSearchRequestsHistory()).thenReturn(setOf())
        mLocalManager.getSearchRequestsHistory()
        verify(mDataStorage, times(1)).getSearchRequestsHistory()
    }

    @Test
    fun setSearchesHistory() = runBlocking {
        val search = "search"
        mLocalManager.setSearchRequestsHistory(setOf(search))
        verify(mDataStorage, times(1)).setSearchRequestsHistory(setOf(search))
    }
}