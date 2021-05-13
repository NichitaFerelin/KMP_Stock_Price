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
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.ferelin.stockprice.ui.MainActivity
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.instanceOf
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/*
 * TODO The test will only work if the application has already been launched before.
 * TODO Can't set the "first run" flag before the test
 * */
@RunWith(AndroidJUnit4::class)
class MainActivityDefaultLaunchTest {

    @get:Rule
    val activityRule = activityScenarioRule<MainActivity>()

    @Test
    fun correctFragmentReplaced() {
        onView(
            allOf(
                instanceOf(TextView::class.java),
                withParent(withId(R.id.cardViewSearch))
            )
        ).check(matches(withText(R.string.hintFindCompany)))
    }
}