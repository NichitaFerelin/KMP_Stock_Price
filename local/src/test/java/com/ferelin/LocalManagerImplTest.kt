package com.ferelin

import com.ferelin.local.LocalManager
import com.ferelin.local.LocalManagerImpl
import com.ferelin.local.database.CompaniesManager
import com.ferelin.local.database.CompaniesManagerImpl
import com.ferelin.local.json.JsonManager
import com.ferelin.local.json.JsonManagerImpl
import com.ferelin.local.preferences.StorePreferences
import com.ferelin.local.preferences.StorePreferencesImpl
import com.ferelin.local.responses.CompaniesResponse
import com.ferelin.local.responses.Responses
import com.ferelin.provider.FakeLocalResponses
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.*
import org.mockito.Spy

class LocalManagerImplTest {

    private lateinit var mLocalManager: LocalManager

    @Spy
    private lateinit var mJsonManager: JsonManager

    @Spy
    private lateinit var mCompaniesManager: CompaniesManager

    @Spy
    private lateinit var mStorePreferences: StorePreferences

    @Before
    fun setUp() {
        mJsonManager = mock(JsonManagerImpl::class.java)
        mCompaniesManager = mock(CompaniesManagerImpl::class.java)
        mStorePreferences = mock(StorePreferencesImpl::class.java)
        mLocalManager = LocalManagerImpl(mJsonManager, mCompaniesManager, mStorePreferences)
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
        `when`(mLocalManager.getCompanies()).thenReturn(flowOf(emptyList()))
        mLocalManager.getCompanies().first()
        verify(mCompaniesManager, times(1)).getCompanies()
    }

    @Test
    fun responseLoadedFromDatabase(): Unit = runBlocking {
        `when`(mCompaniesManager.getCompanies()).thenReturn(flowOf(listOf(FakeLocalResponses.company)))

        val response = mLocalManager.getCompanies().first()
        Assert.assertEquals(true, response is CompaniesResponse.Success)
        Assert.assertEquals(
            Responses.LOADED_FROM_DB,
            (response as CompaniesResponse.Success).code
        )
    }

    @Test
    fun responseLoadedFromJson(): Unit = runBlocking {
        `when`(mCompaniesManager.getCompanies()).thenReturn(flowOf(emptyList()))
        `when`(mLocalManager.getCompaniesFromJson()).thenReturn(flowOf(emptyList()))

        val response = mLocalManager.getCompanies().first()
        Assert.assertEquals(true, response is CompaniesResponse.Success)
        Assert.assertEquals(
            Responses.LOADED_FROM_JSON,
            (response as CompaniesResponse.Success).code
        )
    }

    @Test
    fun getAllCompaniesAsResponseFirstTime(): Unit = runBlocking {
        val item = listOf(FakeLocalResponses.companiesResponseSuccessFromJson.companies.first())

        `when`(mCompaniesManager.getCompanies()).thenReturn(flowOf(emptyList()))
        `when`(mJsonManager.getCompaniesFromJson()).thenReturn(flowOf(item))

        mLocalManager.getCompanies().first()
        verify(mCompaniesManager, times(1)).getCompanies()
        verify(mJsonManager, times(1)).getCompaniesFromJson()
        verify(mCompaniesManager, times(1)).insertAllCompanies(item)
    }

    @Test
    fun getAllCompaniesAsResponse(): Unit = runBlocking {
        `when`(mCompaniesManager.getCompanies())
            .thenReturn(flowOf(listOf(FakeLocalResponses.companiesResponseSuccessFromJson.companies.first())))

        mLocalManager.getCompanies().first()
        verify(mCompaniesManager, times(1)).getCompanies()
        verify(mJsonManager, times(0)).getCompaniesFromJson()
        verify(mCompaniesManager, times(0)).insertAllCompanies(emptyList())
    }

    @Test
    fun getSearchesHistoryAsResponse() {
        mLocalManager.getSearchesHistoryAsResponse()
        verify(mStorePreferences, times(1)).getSearchRequestsHistory()
    }

    @Test
    fun deleteCompany() {
        val item = FakeLocalResponses.companiesResponseSuccessFromJson.companies.first()
        mLocalManager.deleteCompany(item)
        verify(mCompaniesManager, times(1)).deleteCompany(item)
    }

    @Test
    fun getCompaniesFromJson(): Unit = runBlocking {
        `when`(mLocalManager.getCompaniesFromJson()).thenReturn(flowOf(emptyList()))
        mLocalManager.getCompaniesFromJson().firstOrNull()
        verify(mJsonManager, times(1)).getCompaniesFromJson()
    }

    @Test
    fun getSearchesHistory(): Unit = runBlocking {
        `when`(mLocalManager.getSearchRequestsHistory()).thenReturn(flowOf(setOf()))
        mLocalManager.getSearchRequestsHistory().first()
        verify(mStorePreferences, times(1)).getSearchRequestsHistory()
    }

    @Test
    fun setSearchesHistory() = runBlocking {
        val search = "search"
        mLocalManager.setSearchRequestsHistory(setOf(search))
        verify(mStorePreferences, times(1)).setSearchRequestsHistory(setOf(search))
    }
}