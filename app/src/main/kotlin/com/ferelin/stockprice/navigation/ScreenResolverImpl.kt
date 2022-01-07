package com.ferelin.stockprice.navigation

import androidx.fragment.app.*
import com.ferelin.features.splash.ui.LoadingFragment
import com.ferelin.features.about.ui.about.AboutFragment
import com.ferelin.features.authentication.ui.LoginFragment
import com.ferelin.features.search.ui.SearchFragment
import com.ferelin.features.settings.ui.SettingsFragment
import com.ferelin.features.stocks.ui.main.MainFragment
import com.ferelin.navigation.ScreenResolver
import com.ferelin.stockprice.R
import javax.inject.Inject

class ScreenResolverImpl @Inject constructor() : ScreenResolver {

  override fun toLoadingFragment(
    hostActivity: FragmentActivity,
    onTransaction: ((FragmentTransaction) -> Unit)?
  ) {
    replaceMainContainerBy(
      hostActivity.supportFragmentManager,
      LoadingFragment.newInstance(null),
      ScreenResolver.LOADING_TAG,
      false,
      onTransaction
    )
  }

  override fun fromLoadingToStocksPager(
    hostActivity: FragmentActivity,
    params: Any?,
    onTransaction: ((FragmentTransaction) -> Unit)?
  ) {
    replaceMainContainerBy(
      hostActivity.supportFragmentManager,
      MainFragment(params),
      ScreenResolver.STOCKS_PAGER_TAG,
      false,
      onTransaction
    )
  }

  override fun fromStocksPagerToSearch(
    hostActivity: FragmentActivity,
    params: Any?,
    onTransaction: ((FragmentTransaction) -> Unit)?
  ) {
    replaceMainContainerBy(
      hostActivity.supportFragmentManager,
      SearchFragment(params),
      ScreenResolver.SEARCH_TAG,
      true,
      onTransaction
    )
  }

  override fun fromStocksPagerToSettings(
    hostActivity: FragmentActivity,
    params: Any?,
    onTransaction: ((FragmentTransaction) -> Unit)?
  ) {
    replaceMainContainerBy(
      hostActivity.supportFragmentManager,
      SettingsFragment.newInstance(params),
      ScreenResolver.SETTINGS_TAG,
      true,
      onTransaction
    )
  }

  override fun fromDefaultStocksToAbout(
    hostActivity: FragmentActivity,
    params: Any?,
    onTransaction: ((FragmentTransaction) -> Unit)?
  ) {
    val parentManager = hostActivity
      .supportFragmentManager
      .findFragmentByTag(ScreenResolver.STOCKS_PAGER_TAG)
      ?.parentFragmentManager
      ?: throw IllegalStateException(
        "Cannot find fragment by " +
          "tag ${ScreenResolver.STOCKS_PAGER_TAG}"
      )

    replaceMainContainerBy(
      parentManager,
      AboutFragment.newInstance(params),
      ScreenResolver.ABOUT_PAGER_TAG,
      true,
      onTransaction
    )
  }

  override fun fromFavouriteStocksToAbout(
    hostActivity: FragmentActivity,
    params: Any?,
    onTransaction: ((FragmentTransaction) -> Unit)?
  ) {
    val parentManager = hostActivity
      .supportFragmentManager
      .findFragmentByTag(ScreenResolver.STOCKS_PAGER_TAG)
      ?.parentFragmentManager
      ?: throw IllegalStateException(
        "Cannot find fragment by " +
          "tag ${ScreenResolver.STOCKS_PAGER_TAG}"
      )

    replaceMainContainerBy(
      parentManager,
      AboutFragment.newInstance(params),
      ScreenResolver.ABOUT_PAGER_TAG,
      true,
      onTransaction
    )
  }

  override fun fromSearchToAbout(
    hostActivity: FragmentActivity,
    params: Any?,
    onTransaction: ((FragmentTransaction) -> Unit)?
  ) {
    val parentManager = hostActivity
      .supportFragmentManager
      .findFragmentByTag(ScreenResolver.SEARCH_TAG)
      ?.parentFragmentManager
      ?: throw IllegalStateException("Cannot find fragment by tag ${ScreenResolver.SEARCH_TAG}")

    replaceMainContainerBy(
      parentManager,
      SearchFragment.newInstance(params),
      ScreenResolver.ABOUT_PAGER_TAG,
      true,
      onTransaction
    )
  }

  override fun fromSettingsToLogin(
    hostActivity: FragmentActivity,
    params: Any?,
    onTransaction: ((FragmentTransaction) -> Unit)?
  ) {
    replaceMainContainerBy(
      hostActivity.supportFragmentManager,
      LoginFragment.newInstance(params),
      ScreenResolver.LOGIN_TAG,
      true,
      onTransaction
    )
  }

  private fun replaceMainContainerBy(
    fragmentManager: FragmentManager,
    fragment: Fragment,
    tag: String,
    addToBackStack: Boolean = true,
    onTransaction: ((FragmentTransaction) -> Unit)? = null
  ) {
    fragmentManager.commit {
      setReorderingAllowed(true)
      replace(R.id.container, fragment, tag)
      onTransaction?.invoke(this)
      if (addToBackStack) addToBackStack(null)
    }
  }
}