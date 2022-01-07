package com.ferelin.navigation

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction

interface ScreenResolver {
  companion object {
    const val STOCKS_PAGER_TAG = "stocks-pager"
    const val SEARCH_TAG = "search"
    const val ABOUT_PAGER_TAG = "about"
    const val LOGIN_TAG = "login"
    const val SETTINGS_TAG = "settings"
    const val LOADING_TAG = "loading"
  }

  fun toLoadingFragment(
    hostActivity: FragmentActivity,
    onTransaction: ((FragmentTransaction) -> Unit)? = null
  )

  fun fromLoadingToStocksPager(
    hostActivity: FragmentActivity,
    params: Any? = null,
    onTransaction: ((FragmentTransaction) -> Unit)? = null
  )

  fun fromStocksPagerToSearch(
    hostActivity: FragmentActivity,
    params: Any? = null,
    onTransaction: ((FragmentTransaction) -> Unit)? = null
  )

  fun fromStocksPagerToSettings(
    hostActivity: FragmentActivity,
    params: Any? = null,
    onTransaction: ((FragmentTransaction) -> Unit)? = null
  )

  fun fromDefaultStocksToAbout(
    hostActivity: FragmentActivity,
    params: Any? = null,
    onTransaction: ((FragmentTransaction) -> Unit)? = null
  )

  fun fromFavouriteStocksToAbout(
    hostActivity: FragmentActivity,
    params: Any? = null,
    onTransaction: ((FragmentTransaction) -> Unit)? = null
  )

  fun fromSearchToAbout(
    hostActivity: FragmentActivity,
    params: Any? = null,
    onTransaction: ((FragmentTransaction) -> Unit)? = null
  )

  fun fromSettingsToLogin(
    hostActivity: FragmentActivity,
    params: Any? = null,
    onTransaction: ((FragmentTransaction) -> Unit)? = null
  )
}