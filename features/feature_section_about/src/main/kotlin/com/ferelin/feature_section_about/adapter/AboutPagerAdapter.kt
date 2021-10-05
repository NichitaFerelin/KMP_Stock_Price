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

package com.ferelin.feature_section_about.adapter

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ferelin.feature_chart.view.ChartFragment
import com.ferelin.feature_forecasts.ForecastsFragment
import com.ferelin.feature_ideas.IdeasFragment
import com.ferelin.feature_news.view.NewsFragment
import com.ferelin.feature_profile.view.ProfileFragment

class AboutPagerAdapter(
    private val mArguments: Bundle,
    fm: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProfileFragment.newInstance(mArguments)
            1 -> ChartFragment.newInstance(mArguments)
            2 -> NewsFragment.newInstance(mArguments)
            3 -> ForecastsFragment()
            4 -> IdeasFragment()
            else -> throw IllegalStateException("No fragment for position: $position")
        }
    }
}