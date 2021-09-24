package com.ferelin

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.ferelin.local.dataStorage.DataStorage
import com.ferelin.local.dataStorage.DataStorageImpl
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class StorePreferencesImplTest {

    private lateinit var mDataStorage: DataStorage

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        mDataStorage = DataStorageImpl(context)
    }

    @Test
    fun get_setSearchesHistory(): Unit = runBlocking {
        val requests = setOf("first", "second")
        mDataStorage.setSearchRequestsHistory(requests)
        mDataStorage.getSearchRequestsHistory().first().also {
            Assert.assertEquals(requests, it)
        }
    }

    @Test
    fun get_setFirstTimeLaunch() : Unit = runBlocking {
        mDataStorage.setFirstTimeLaunchState(true)
        mDataStorage.observeFirstTimeLaunch().also {
            Assert.assertEquals(it, true)
        }
    }
}