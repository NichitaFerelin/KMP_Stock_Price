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

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ferelin.core.params.AboutParams
import com.ferelin.core.params.ChartParams
import com.ferelin.core.params.NewsParams
import com.ferelin.core.params.ProfileParams
import com.ferelin.feature_chart.view.ChartFragment
import com.ferelin.feature_forecasts.ForecastsFragment
import com.ferelin.feature_ideas.IdeasFragment
import com.ferelin.feature_news.view.NewsFragment
import com.ferelin.feature_profile.view.ProfileFragment

class AboutPagerAdapter(
    private val params: AboutParams,
    fm: FragmentManager,
    lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {

    override fun getItemCount(): Int {
        return 3
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ProfileFragment.newInstance(
                ProfileParams(
                    params.companyId,
                    params.companyTicker,
                    params.companyName,
                    params.logoUrl
                )
            )

            1 -> ChartFragment.newInstance(
                ChartParams(
                    params.companyId,
                    params.companyTicker,
                    params.stockPrice,
                    params.stockProfit
                )
            )

            2 -> NewsFragment.newInstance(
                NewsParams(
                    params.companyId,
                    params.companyTicker
                )
            )

            3 -> ForecastsFragment.newInstance(null)
            4 -> IdeasFragment.newInstance(null)
            else -> throw IllegalStateException("No fragment for position: $position")
        }
    }
}