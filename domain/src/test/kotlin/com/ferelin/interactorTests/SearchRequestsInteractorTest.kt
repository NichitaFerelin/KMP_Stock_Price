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

package com.ferelin.interactorTests

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.ferelin.di.DaggerTestAppComponent
import com.ferelin.domain.interactors.searchRequests.SearchRequestsInteractor
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
class SearchRequestsInteractorTest {

    lateinit var testCoroutineDispatcher: TestCoroutineDispatcher

    @Inject
    lateinit var searchRequestsInteractor: SearchRequestsInteractor

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
    fun cacheWhenDbIsEmpty() = testCoroutineDispatcher.runBlockingTest {
        searchRequestsInteractor.getAll()

        val fakeSearch = "search"
        searchRequestsInteractor.cache(fakeSearch)

        val actual = searchRequestsInteractor.getAll()
        Assert.assertEquals(1, actual.size)
        Assert.assertEquals(actual[0].request, fakeSearch)
    }

    @Test
    fun reordering() = testCoroutineDispatcher.runBlockingTest {
        searchRequestsInteractor.getAll()

        FakeData.duplicatedStrings.forEach {
            searchRequestsInteractor.cache(it)
        }

        val actual = searchRequestsInteractor.getAll().map { it.request }
        Assert.assertEquals(4, actual.size)

        // Assert.assertEquals(FakeData.reorderingStrings, actual)
    }

    @Test
    fun getAll() = testCoroutineDispatcher.runBlockingTest {
        FakeData.uniqueSearchRequests.forEach {
            searchRequestsLocalRepo.insert(it)
        }

        val actual = searchRequestsInteractor.getAll()
        Assert.assertEquals(FakeData.uniqueSearchRequests.size, actual.size)
    }

    @Test
    fun eraseUserData() = testCoroutineDispatcher.runBlockingTest {

        FakeData.uniqueSearchRequests.forEach {
            searchRequestsLocalRepo.insert(it)
        }

        searchRequestsInteractor.eraseUserData()

        val actual = searchRequestsInteractor.getAll()
        Assert.assertEquals(0, actual.size)
    }
}