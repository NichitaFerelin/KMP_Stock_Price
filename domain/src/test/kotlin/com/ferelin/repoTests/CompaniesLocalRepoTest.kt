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
import com.ferelin.domain.repositories.companies.CompaniesLocalRepo
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
class CompaniesLocalRepoTest {

    lateinit var testCoroutineDispatcher: TestCoroutineDispatcher

    @Inject
    lateinit var companiesLocalRepo: CompaniesLocalRepo

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
        companiesLocalRepo.insertAll(FakeData.companies)

        val actual = companiesLocalRepo.getAll()
        Assert.assertEquals(FakeData.companies.size, actual.size)
    }

    @Test
    fun getAll() = testCoroutineDispatcher.runBlockingTest {
        companiesLocalRepo.insertAll(FakeData.companies)

        val actual = companiesLocalRepo.getAll()
        Assert.assertEquals(FakeData.companies.size, actual.size)

        actual.forEach {
            val exists = FakeData.companies.contains(it.company)
            Assert.assertEquals(true, exists)
        }
    }

    @Test
    fun getAllFavourites() = testCoroutineDispatcher.runBlockingTest {
        companiesLocalRepo.insertAll(FakeData.companiesWithFavourites)

        val actual = companiesLocalRepo.getAllFavourites()
        Assert.assertEquals(FakeData.favouriteCompaniesCount, actual.size)

        actual.forEach {
            val exists = FakeData.companiesWithFavourites.contains(it.company)
            Assert.assertEquals(true, exists)
        }
    }

    @Test
    fun rollbackToDefault() = testCoroutineDispatcher.runBlockingTest {
        companiesLocalRepo.insertAll(FakeData.companiesWithFavourites)
        companiesLocalRepo.rollbackToDefault()

        val actual = companiesLocalRepo.getAllFavourites()
        Assert.assertEquals(0, actual.size)
    }

    @Test
    fun updateIsFavouriteTrue() = testCoroutineDispatcher.runBlockingTest {
        companiesLocalRepo.insertAll(FakeData.companies)

        companiesLocalRepo.updateIsFavourite(FakeData.companies[1].id, true, 1)
        companiesLocalRepo.updateIsFavourite(FakeData.companies[2].id, true, 2)
        companiesLocalRepo.updateIsFavourite(FakeData.companies[3].id, true, 3)

        val actual = companiesLocalRepo.getAllFavourites()
        Assert.assertEquals(3, actual.size)
    }

    @Test
    fun updateIsFavouriteFalse() = testCoroutineDispatcher.runBlockingTest {
        companiesLocalRepo.insertAll(FakeData.companiesWithFavourites)

        companiesLocalRepo.updateIsFavourite(FakeData.companies[1].id, false)
        companiesLocalRepo.updateIsFavourite(FakeData.companies[4].id, false)

        val actual = companiesLocalRepo.getAllFavourites()
        Assert.assertEquals(FakeData.favouriteCompaniesCount - 2, actual.size)
    }
}