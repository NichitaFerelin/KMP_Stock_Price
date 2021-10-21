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

package com.ferelin.stockprice

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.swipeLeft
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import com.ferelin.core.adapter.stocks.StockViewHolder
import com.ferelin.stockprice.ui.MainActivity
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class AboutPagerFragmentTest {

    @get:Rule
    val activityRule = activityScenarioRule<MainActivity>()

    private val device = UiDevice.getInstance(getInstrumentation())

    @Before
    fun replaceFragment() {
        onView(withId(R.id.recyclerViewStocks))
            .perform(RecyclerViewActions.actionOnItemAtPosition<StockViewHolder>(0, click()))
    }

    @Test
    fun widgetsDisplayed() {
        onView(withId(R.id.textViewCompanyTicker)).check(matches(isDisplayed()))
        onView(withId(R.id.textViewCompanyName)).check(matches(isDisplayed()))
        onView(withId(R.id.imageViewBack)).check(matches(isDisplayed()))
        onView(withId(R.id.imageViewStar)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToChartUsingSwipes() {
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        onView(withId(R.id.rootChart)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToNewsUsingSwipes() {
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        onView(withId(R.id.recyclerViewNews)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToForecastsUsingSwipes() {
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        onView(withId(R.id.textViewForecastsTitle)).check(matches(isDisplayed()))

    }

    @Test
    fun navigateToIdeasUsingSwipes() {
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        onView(withId(R.id.textViewIdeasTitle)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateBackToProfileUsingBackPressed() {
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        device.pressBack()
        onView(withId(R.id.constraintRoot)).check(matches(isDisplayed()))
    }

    @Test
    fun saveStateAfterRotation() {
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        onView(withId(R.id.viewPager)).perform(swipeLeft())
        device.setOrientationRight()
        onView(withId(R.id.textViewIdeasTitle)).check(matches(isDisplayed()))
    }
}