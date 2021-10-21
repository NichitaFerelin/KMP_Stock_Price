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
import com.ferelin.domain.entities.CompanyWithStockPrice
import com.ferelin.domain.interactors.companies.CompaniesInteractor
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
class CompaniesInteractorTest {

    lateinit var testCoroutineDispatcher: TestCoroutineDispatcher

    @Inject
    lateinit var companiesInteractor: CompaniesInteractor

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
    fun getAllCompanies(): Unit = testCoroutineDispatcher.runBlockingTest {
        val all = companiesInteractor.getAll()

        // Json with data has 1487 items
        Assert.assertEquals(1487, all.size)
    }

    @Test
    fun getAllFavouriteCompanies(): Unit = testCoroutineDispatcher.runBlockingTest {
        val addedToFavourites = mutableListOf<CompanyWithStockPrice>()
        val companiesToAdd = 100
        val all = companiesInteractor.getAll()

        repeat(companiesToAdd) {
            val randomItem = all.random()
            addedToFavourites.add(randomItem)
            companiesInteractor.addCompanyToFavourites(randomItem.company)
        }

        val cachedFavourites = companiesInteractor.getAllFavourites()

        Assert.assertEquals(companiesToAdd, cachedFavourites.size)

        addedToFavourites.forEach {
            val exists = cachedFavourites.contains(it)
            Assert.assertEquals(true, exists)
        }
    }

    @Test
    fun addCompanyToFavourites(): Unit = testCoroutineDispatcher.runBlockingTest {
        val randomItem = companiesInteractor.getAll().random()
        companiesInteractor.addCompanyToFavourites(randomItem.company)

        val exists = companiesInteractor
            .getAllFavourites()
            .contains(randomItem)

        Assert.assertEquals(true, exists)
    }

    @Test
    fun addCompanyToFavouritesById(): Unit = testCoroutineDispatcher.runBlockingTest {
        val randomItem = companiesInteractor.getAll().random()
        companiesInteractor.addCompanyToFavourites(randomItem.company.id)

        val exists = companiesInteractor
            .getAllFavourites()
            .contains(randomItem)

        Assert.assertEquals(true, exists)
    }

    @Test
    fun eraseCompanyFromFavourites(): Unit = testCoroutineDispatcher.runBlockingTest {
        val randomItem = companiesInteractor.getAll().random()
        companiesInteractor.addCompanyToFavourites(randomItem.company.id)
        companiesInteractor.eraseCompanyFromFavourites(randomItem.company)

        val exists = companiesInteractor
            .getAllFavourites()
            .contains(randomItem)

        Assert.assertEquals(false, exists)
    }

    @Test
    fun eraseCompanyFromFavouritesById(): Unit = testCoroutineDispatcher.runBlockingTest {
        val randomItem = companiesInteractor.getAll().random()
        companiesInteractor.addCompanyToFavourites(randomItem.company.id)
        companiesInteractor.eraseCompanyFromFavourites(randomItem.company.id)

        val exists = companiesInteractor
            .getAllFavourites()
            .contains(randomItem)

        Assert.assertEquals(false, exists)
    }

    @Test
    fun eraseUserData(): Unit = testCoroutineDispatcher.runBlockingTest {
        val all = companiesInteractor.getAll()

        repeat(100) {
            val randomItem = all.random()
            companiesInteractor.addCompanyToFavourites(randomItem.company)
        }

        companiesInteractor.eraseUserData()

        val favouriteCompanies = companiesInteractor.getAllFavourites()
        Assert.assertEquals(0, favouriteCompanies.size)
    }
}