/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ferelin

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.ferelin.local.json.JsonAssets
import com.ferelin.local.json.JsonAssetsReader
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Spy
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class JsonAssetsReaderTest {

    private lateinit var mJsonReader: JsonAssetsReader

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        mJsonReader = JsonAssetsReader(context, JsonAssets.COMPANIES)
    }

    @Test
    fun correctRead(): Unit = runBlocking {
        val companiesInAsset = 1487
        mJsonReader.readCompanies().first().also {
            Assert.assertEquals(companiesInAsset, it.size)
        }
    }

    @Test
    fun readWithException(): Unit = runBlocking {
        val context = ApplicationProvider.getApplicationContext<Context>()
        mJsonReader = JsonAssetsReader(context, "Wrong file name")
        mJsonReader.readCompanies().first().also {
            Assert.assertEquals(0, it.size)
        }
    }
}