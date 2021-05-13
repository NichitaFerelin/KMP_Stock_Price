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
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.contrib.RecyclerViewActions
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ferelin.stockprice.ui.MainActivity
import com.ferelin.stockprice.ui.stocksSection.common.StockViewHolder
import org.hamcrest.Matchers.allOf
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProfileFragmentTest {

    @get:Rule
    val activityRule = activityScenarioRule<MainActivity>()

    @Before
    fun replaceFragment() {
        onView(withId(R.id.recyclerViewStocks))
            .perform(RecyclerViewActions.actionOnItemAtPosition<StockViewHolder>(0, click()))
    }

    @Test
    fun widgetsDisplayed() {
        onView(withId(R.id.constraintRoot)).check(matches(isDisplayed()))
        onView(withId(R.id.cardViewIcon)).check(matches(isDisplayed()))
        onView(withId(R.id.imageViewIcon)).check(matches(isDisplayed()))
        onView(withId(R.id.textViewName)).check(matches(isDisplayed()))
        onView(withId(R.id.textViewWebUrl)).check(matches(isDisplayed()))
        onView(withId(R.id.viewLine)).check(matches(isDisplayed()))
        onView(withId(R.id.textViewCountry)).check(matches(isDisplayed()))
        onView(withId(R.id.textViewIndustry)).check(matches(isDisplayed()))
        onView(withId(R.id.textViewPhone)).check(matches(isDisplayed()))
        onView(withId(R.id.textViewCapitalization)).check(matches(isDisplayed()))

        onView(withId(R.id.textViewHintName))
            .check(matches(allOf(withText(R.string.hintName), isDisplayed())))
        onView(withId(R.id.textViewHintWebUrl))
            .check(matches(allOf(withText(R.string.hintWebsite), isDisplayed())))
        onView(withId(R.id.textViewHintCountry))
            .check(matches(allOf(withText(R.string.hintCountry), isDisplayed())))
        onView(withId(R.id.textViewHintIndustry))
            .check(matches(allOf(withText(R.string.hintIndustry), isDisplayed())))
        onView(withId(R.id.textViewHintPhone))
            .check(matches(allOf(withText(R.string.hintPhone), isDisplayed())))
        onView(withId(R.id.textViewHintCapitalization))
            .check(matches(allOf(withText(R.string.hintCapitalization), isDisplayed())))
    }
}