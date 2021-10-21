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
import com.ferelin.domain.repositories.FirstLaunchRepo
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestCoroutineDispatcher
import org.junit.After
import org.junit.Before
import org.robolectric.annotation.Config
import javax.inject.Inject

// TODO job is not completed yet exception

@Config(manifest = Config.NONE)
// @RunWith(RobolectricTestRunner::class)
class FirstLaunchRepoTest {

    lateinit var testCoroutineDispatcher: TestCoroutineDispatcher

    @Inject
    lateinit var firstLaunchRepo: FirstLaunchRepo

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

   /* @Test
    fun get() = testCoroutineDispatcher.runBlockingTest {
        firstLaunchRepo.cache(true)
        val actual = firstLaunchRepo.get()
        Assert.assertEquals(true, actual)
    }

    @Test
    fun getWhenNull() = testCoroutineDispatcher.runBlockingTest {
        val actual = firstLaunchRepo.get()
        Assert.assertEquals(null, actual)
    }

    @Test
    fun cacheTrue() = testCoroutineDispatcher.runBlockingTest {
        firstLaunchRepo.cache(true)
        val actual = firstLaunchRepo.get()
        Assert.assertEquals(true, actual)
    }

    @Test
    fun cacheFalse() = testCoroutineDispatcher.runBlockingTest {
        firstLaunchRepo.cache(false)
        val actual = firstLaunchRepo.get()
        Assert.assertEquals(false, actual)
    }*/
}

