package com.ferelin.features.about.ui.about

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ferelin.core.ui.params.AboutParams
import com.ferelin.core.ui.params.ChartParams
import com.ferelin.core.ui.params.NewsParams
import com.ferelin.core.ui.params.ProfileParams
import com.ferelin.features.about.ui.chart.ChartFragment
import com.ferelin.features.about.ui.news.NewsFragment
import com.ferelin.features.about.ui.profile.ProfileFragment

internal class AboutViewAdapter(
  private val params: AboutParams,
  fm: FragmentManager,
  lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {
  override fun getItemCount(): Int = 3

  override fun createFragment(position: Int): Fragment {
    return when (position) {
      0 -> ProfileFragment(ProfileParams(params.companyId))
      1 -> ChartFragment(ChartParams(params.companyId, params.companyTicker))
      2 -> NewsFragment(NewsParams(params.companyId, params.companyTicker))
      else -> error("No fragment for position: $position")
    }
  }
}