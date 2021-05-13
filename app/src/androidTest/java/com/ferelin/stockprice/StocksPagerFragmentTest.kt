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

import android.widget.TextView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions.actionOnItemAtPosition
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import com.ferelin.stockprice.ui.MainActivity
import com.ferelin.stockprice.ui.stocksSection.common.StockViewHolder
import org.hamcrest.CoreMatchers.*
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class StocksPagerFragmentTest {

    @get:Rule
    val activityRule = activityScenarioRule<MainActivity>()

    private val device = UiDevice.getInstance(getInstrumentation())

    @Test
    fun widgetsDisplayed() {
        onView(withId(R.id.appBar)).check(matches(isDisplayed()))
        onView(withId(R.id.toolbar)).check(matches(isDisplayed()))
        onView(withId(R.id.cardViewSearch)).check(matches(isDisplayed()))
        onView(withId(R.id.linearHeader)).check(matches(isDisplayed()))
        onView(withId(R.id.recyclerViewStocks)).check(matches(isDisplayed()))

        onView(
            allOf(
                instanceOf(TextView::class.java),
                withId(R.id.textViewHintStocks),
                withParent(withId(R.id.linearHeader))
            )
        ).check(matches(allOf(isDisplayed(), withText(R.string.titleStocks))))

        onView(
            allOf(
                instanceOf(TextView::class.java),
                withId(R.id.textViewHintFavourite),
                withParent(withId(R.id.linearHeader))
            )
        ).check(matches(allOf(isDisplayed(), withText(R.string.titleFavourite))))
    }

    @Test
    fun navigateToFavouritesStocksUsingTap() {
        onView(withId(R.id.textViewHintFavourite)).perform(click())
        onView(withId(R.id.recyclerViewFavourites)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToFavouritesStocksUsingSwipe() {
        onView(withId(R.id.textViewHintFavourite)).perform(swipeLeft())
        onView(withId(R.id.recyclerViewFavourites)).check(matches(isDisplayed()))
    }

    @Test
    fun savePagerStateAfterRotating() {
        onView(withId(R.id.textViewHintFavourite)).perform(swipeLeft())
        device.setOrientationRight()
        onView(withId(R.id.recyclerViewFavourites)).check(matches(isDisplayed()))
    }

    @Test
    fun navigateToSearchFragment() {
        onView(withId(R.id.toolbar)).perform(click())
        onView(
            allOf(
                instanceOf(TextView::class.java),
                withId(R.id.textViewRequests),
            )
        ).check(matches(withText(R.string.titlePopularRequests)))
        onView(
            allOf(
                instanceOf(TextView::class.java),
                withId(R.id.textViewSearched),
            )
        ).check(matches(withText(R.string.titleYourSearches)))
    }

    @Test
    fun navigateToFirstFragmentWhenSecondFragmentIsOpenByBackPress() {
        onView(withId(R.id.textViewHintFavourite)).perform(swipeLeft())
        device.pressBack()
        onView(withId(R.id.recyclerViewStocks)).check(matches(isDisplayed()))
    }

    /*@Test
    fun fabIsVisibleOnScrollDown() {
        onView(withId(R.id.recyclerViewStocks))
            .perform(actionOnItemAtPosition<StockViewHolder>(1, scrollTo()))
        onView(withId(R.id.fab)).check(matches(isDisplayed()))
    }

    @Test
    fun fabIsGoneOnScrollTop() {
        onView(withId(R.id.recyclerViewStocks))
            .perform(actionOnItemAtPosition<StockViewHolder>(1, scrollTo()))
        onView(withId(R.id.recyclerViewStocks))
            .perform(actionOnItemAtPosition<StockViewHolder>(0, scrollTo()))
        onView(withId(R.id.fab)).check(matches(not(isDisplayed())))
    }*/
}