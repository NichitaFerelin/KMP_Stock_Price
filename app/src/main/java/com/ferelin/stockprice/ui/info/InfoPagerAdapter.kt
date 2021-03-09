package com.ferelin.stockprice.ui.info

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ferelin.stockprice.ui.chart.ChartFragment
import com.ferelin.stockprice.ui.forecasts.ForecastsFragment
import com.ferelin.stockprice.ui.ideas.IdeasFragment
import com.ferelin.stockprice.ui.news.NewsFragment
import com.ferelin.stockprice.ui.summary.SummaryFragment

class InfoPagerAdapter(
    fm: FragmentManager,
    lifecycle: Lifecycle,
    arguments: Bundle
) : FragmentStateAdapter(fm, lifecycle) {

    private val mArguments: Bundle = arguments

    override fun getItemCount(): Int {
        return 5
    }

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> ChartFragment.newInstance(
                mArguments[CURRENT_PRICE_KEY] as String,
                mArguments[DAY_DELTA_KEY] as String
            )
            1 -> SummaryFragment()
            2 -> NewsFragment()
            3 -> ForecastsFragment()
            4 -> IdeasFragment()
            else -> throw IllegalStateException("No fragment for position: $position")
        }
    }

    companion object {
        const val CURRENT_PRICE_KEY = "current_price_key"
        const val DAY_DELTA_KEY = "day_delta_key"
    }
}