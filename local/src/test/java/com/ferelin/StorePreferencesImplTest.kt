package com.ferelin

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.ferelin.local.preferences.StorePreferences
import com.ferelin.local.preferences.StorePreferencesImpl
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

    private lateinit var mStorePreferences: StorePreferences

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        mStorePreferences = StorePreferencesImpl(context)
    }

    @Test
    fun get_setSearchesHistory(): Unit = runBlocking {
        val requests = setOf("first", "second")
        mStorePreferences.setSearchRequestsHistory(requests)
        mStorePreferences.getSearchRequestsHistory().first().also {
            Assert.assertEquals(requests, it)
        }
    }

    @Test
    fun get_setFirstTimeLaunch() : Unit = runBlocking {
        mStorePreferences.setFirstTimeLaunchState(true)
        mStorePreferences.getFirstTimeLaunchState().also {
            Assert.assertEquals(it, true)
        }
    }
}