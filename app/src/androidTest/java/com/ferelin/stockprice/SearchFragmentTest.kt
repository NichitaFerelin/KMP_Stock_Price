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
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.action.ViewActions.replaceText
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import androidx.test.uiautomator.UiDevice
import com.ferelin.stockprice.ui.MainActivity
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.instanceOf
import org.hamcrest.Matchers.not
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SearchFragmentTest {

    @get:Rule
    val activityRule = activityScenarioRule<MainActivity>()

    private val device = UiDevice.getInstance(getInstrumentation())

    @Before
    fun replaceFragment() {
        onView(withId(R.id.toolbar)).perform(click())
    }

    @Test
    fun widgetsDisplayed() {
        onView(withId(R.id.cardViewSearch)).check(matches(isDisplayed()))
        onView(withId(R.id.imageViewBack)).check(matches(isDisplayed()))
        onView(withId(R.id.editTextSearch)).check(matches(isDisplayed()))
        onView(withId(R.id.recyclerViewPopularRequests)).check(matches(isDisplayed()))
        onView(withId(R.id.recyclerViewSearchedHistory)).check(matches(isDisplayed()))

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
    fun isKeyboardOpen() {
        val checkKeyboardCmd = "dumpsys input_method | grep mInputShown"
        val isKeyboardActualOpen = device
            .executeShellCommand(checkKeyboardCmd)
            .contains("mInputShown=true")
        Assert.assertEquals(true, isKeyboardActualOpen)
    }

    @Test
    fun btnCloseIsVisibleWhenSearchTextIsNotEmpty() {
        onView(withId(R.id.editTextSearch)).perform(replaceText("Random text"))
        onView(withId(R.id.imageViewIconClose)).check(matches(isDisplayed()))
    }

    @Test
    fun clearSearchTextByBackPress() {
        onView(withId(R.id.editTextSearch)).perform(replaceText("Random text"))
        device.pressBack() // close Keyboard
        device.pressBack()
        onView(withId(R.id.editTextSearch)).check(matches(withText("")))
    }

    /*@Test
    fun btnCloseIsNotVisibleWhenSearchTextIsEmpty() {
        onView(withId(R.id.editTextSearch)).perform(clearText())
        onView(withId(R.id.imageViewIconClose)).check(matches(not(isDisplayed())))
    }*/

    @Test
    fun searchTextIsEmptyAfterCloseBtnClicked() {
        onView(withId(R.id.editTextSearch)).perform(replaceText("Random text"))
        onView(withId(R.id.imageViewIconClose)).perform(click())
        onView(withId(R.id.editTextSearch)).check(matches(withText("")))
    }

    @Test
    fun transitionToEndWhenSearchResultsPresent() {
        onView(withId(R.id.editTextSearch)).perform(replaceText("Apple"))
        onView(withId(R.id.recyclerViewSearchResults)).check(matches(isDisplayed()))
    }

    @Test
    fun transitionToStartWhenSearchResultsAreNotPresent() {
        onView(withId(R.id.editTextSearch)).perform(replaceText("Random text"))
        onView(withId(R.id.recyclerViewSearchResults)).check(matches(not(isDisplayed())))
    }

    @Test
    fun saveTransitionStateAfterRotation() {
        onView(withId(R.id.editTextSearch)).perform(replaceText("Apple"))
        device.setOrientationRight()
        onView(withId(R.id.recyclerViewSearchResults)).check(matches(isDisplayed()))
    }
}