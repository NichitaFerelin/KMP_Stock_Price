package com.ferelin.stockprice.ui.aboutSection.aboutPager

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

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.ui.aboutSection.chart.ChartFragment
import com.ferelin.stockprice.ui.aboutSection.forecasts.ForecastsFragment
import com.ferelin.stockprice.ui.aboutSection.ideas.IdeasFragment
import com.ferelin.stockprice.ui.aboutSection.news.NewsFragment
import com.ferelin.stockprice.ui.aboutSection.profile.ProfileFragment

class AboutPagerAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle,
    selectedCompany: AdaptiveCompany?
) : FragmentStateAdapter(fm, lifecycle) {

    private val mSelectedCompany: AdaptiveCompany? = selectedCompany

    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProfileFragment(mSelectedCompany = mSelectedCompany!!)
            1 -> ChartFragment(mSelectedCompany)
            2 -> NewsFragment(mSelectedCompany)
            3 -> ForecastsFragment()
            4 -> IdeasFragment()
            else -> throw IllegalStateException("No fragment for position: $position")
        }
    }
}