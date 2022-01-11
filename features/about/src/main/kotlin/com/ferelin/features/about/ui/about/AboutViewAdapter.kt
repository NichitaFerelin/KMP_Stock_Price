package com.ferelin.features.about.ui.about

import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.ferelin.core.ui.params.AboutParams
import com.ferelin.core.ui.params.ChartParams
import com.ferelin.core.ui.params.NewsParams
import com.ferelin.core.ui.params.ProfileParams
import com.ferelin.core.ui.view.ScreenKey
import com.ferelin.features.about.ui.chart.ChartScreenKey
import com.ferelin.features.about.ui.news.NewsScreenKey
import com.ferelin.features.about.ui.profile.ProfileScreenKey

internal class AboutViewAdapter(
  private val params: AboutParams,
  fm: FragmentManager,
  lifecycle: Lifecycle
) : FragmentStateAdapter(fm, lifecycle) {
  override fun getItemCount(): Int = 3

  override fun createFragment(position: Int): Fragment {
    return when (position) {
      0 -> {
        create(
          ProfileScreenKey,
          bundleOf(
            ProfileScreenKey.controllerConfig.key to ProfileParams(
              params.companyId
            )
          )
        )
      }
      1 -> {
        create(
          ChartScreenKey,
          bundleOf(
            ChartScreenKey.controllerConfig.key to ChartParams(
              params.companyId,
              params.companyTicker
            )
          )
        )
      }
      2 -> {
        create(
          NewsScreenKey,
          bundleOf(
            NewsScreenKey.controllerConfig.key to NewsParams(
              params.companyId,
              params.companyTicker
            )
          )
        )
      }
      else -> error("No fragment for position: $position")
    }
  }

  private fun create(screenKey: ScreenKey, args: Bundle): Fragment {
    return screenKey.controllerConfig.controllerClass.newInstance()
      .apply { arguments = args }
  }
}