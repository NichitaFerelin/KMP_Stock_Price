package com.ferelin

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.ferelin.local.preferences.StorePreferences
import com.ferelin.local.preferences.StorePreferencesHelper
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class StorePreferencesTest {

    private lateinit var mStorePreferences: StorePreferencesHelper

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        mStorePreferences = StorePreferences(context)
    }

    @Test
    fun common(): Unit = runBlocking {
        val requests = setOf("first", "second")
        mStorePreferences.setSearchesHistory(requests)
        mStorePreferences.getSearchesHistory().first().also {
            Assert.assertEquals(requests, it)
        }
    }
}