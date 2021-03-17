package com.ferelin.stockprice.ui.aboutSection.aboutPager

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.ui.aboutSection.chart.ChartFragment
import com.ferelin.stockprice.ui.aboutSection.forecasts.ForecastsFragment
import com.ferelin.stockprice.ui.aboutSection.ideas.IdeasFragment
import com.ferelin.stockprice.ui.aboutSection.news.NewsFragment
import com.ferelin.stockprice.ui.aboutSection.summary.SummaryFragment

class AboutPagerAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle,
    ownerCompany: AdaptiveCompany?
) : FragmentStateAdapter(fm, lifecycle) {

    private val mOwnerCompany: AdaptiveCompany? = ownerCompany

    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ChartFragment(mOwnerCompany)
            1 -> SummaryFragment()
            2 -> NewsFragment(mOwnerCompany)
            3 -> ForecastsFragment()
            4 -> IdeasFragment()
            else -> throw IllegalStateException("No fragment for position: $position")
        }
    }
}