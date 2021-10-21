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
import com.ferelin.domain.entities.SearchRequest
import com.ferelin.domain.repositories.searchRequests.SearchRequestsLocalRepo
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
class SearchRequestsLocalRepoTest {

    lateinit var testCoroutineDispatcher: TestCoroutineDispatcher

    @Inject
    lateinit var searchRequestsLocalRepo: SearchRequestsLocalRepo

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
    fun insert() = testCoroutineDispatcher.runBlockingTest {
        val searchRequest = SearchRequest(1, "test")
        searchRequestsLocalRepo.insert(searchRequest)

        val actual = searchRequestsLocalRepo.getAll()
        Assert.assertEquals(1, actual.size)
        Assert.assertEquals(searchRequest, actual.first())
    }

    @Test
    fun getAll() = testCoroutineDispatcher.runBlockingTest {
        FakeData.uniqueSearchRequests.forEach {
            searchRequestsLocalRepo.insert(it)
        }

        val actual = searchRequestsLocalRepo.getAll()
        Assert.assertEquals(FakeData.uniqueSearchRequests.size, actual.size)

        FakeData.uniqueSearchRequests.forEach {
            val exists = actual.contains(it)
            Assert.assertEquals(true, exists)
        }
    }

    @Test
    fun eraseAll() = testCoroutineDispatcher.runBlockingTest {
        FakeData.uniqueSearchRequests.forEach {
            searchRequestsLocalRepo.insert(it)
        }

        searchRequestsLocalRepo.eraseAll()

        val actual = searchRequestsLocalRepo.getAll()
        Assert.assertEquals(0, actual.size)
    }

    @Test
    fun erase() = testCoroutineDispatcher.runBlockingTest {
        FakeData.uniqueSearchRequests.forEach {
            searchRequestsLocalRepo.insert(it)
        }

        searchRequestsLocalRepo.erase(FakeData.uniqueSearchRequests[0])
        searchRequestsLocalRepo.erase(FakeData.uniqueSearchRequests[1])

        val actual = searchRequestsLocalRepo.getAll()
        Assert.assertEquals(FakeData.uniqueSearchRequests.size - 2, actual.size)

        val firstExists = actual.contains(FakeData.uniqueSearchRequests[0])
        val secondExists = actual.contains(FakeData.uniqueSearchRequests[1])

        Assert.assertEquals(false, firstExists)
        Assert.assertEquals(false, secondExists)
    }
}