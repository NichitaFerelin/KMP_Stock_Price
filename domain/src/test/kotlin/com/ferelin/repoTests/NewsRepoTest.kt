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

package com.ferelin.repoTests

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.ferelin.di.DaggerTestAppComponent
import com.ferelin.domain.repositories.NewsRepo
import com.ferelin.fakeData.FakeData
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.runBlockingTest
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import javax.inject.Inject

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class NewsRepoTest {

    lateinit var testCoroutineDispatcher: TestCoroutineDispatcher

    @Inject
    lateinit var newsRepo: NewsRepo

    @Before
    fun before() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val component = DaggerTestAppComponent.factory().create(context)
        component.inject(this)

        testCoroutineDispatcher = TestCoroutineDispatcher()
    }

    @After
    fun after() {
        testCoroutineDispatcher.cancel()
    }

    @Test
    fun insertAll() = testCoroutineDispatcher.runBlockingTest {
        newsRepo.insertAll(FakeData.news)
        val actual = newsRepo.getAllBy(FakeData.relationId) +
                newsRepo.getAllBy(0) + newsRepo.getAllBy(3) +
                newsRepo.getAllBy(7)

        Assert.assertEquals(FakeData.news.size, actual.size)
    }

    @Test
    fun getAllBy() = testCoroutineDispatcher.runBlockingTest {
        newsRepo.insertAll(FakeData.news)
        val actual = newsRepo.getAllBy(FakeData.relationId)

        Assert.assertEquals(FakeData.defaultSizeByRelationId, actual.size)

        actual.forEach {
            Assert.assertEquals(FakeData.relationId, it.relationCompanyId)
        }
    }

    @Test
    fun eraseBy() = testCoroutineDispatcher.runBlockingTest {
        newsRepo.insertAll(FakeData.news)
        newsRepo.eraseBy(FakeData.relationId)

        val actual = newsRepo.getAllBy(FakeData.relationId)

        Assert.assertEquals(0, actual.size)
    }
}