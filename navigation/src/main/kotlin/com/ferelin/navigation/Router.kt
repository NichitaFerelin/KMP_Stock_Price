package com.ferelin.navigation

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction

interface Router {

  fun bind(activity: FragmentActivity)
  fun unbind()
  fun back()
  fun openUrl(url: String): Boolean
  fun openContacts(phone: String): Boolean
  fun shareText(text: String)
  fun toStartFragment()

  fun fromLoadingToStocksPager(
    params: Any? = null,
    onTransaction: ((FragmentTransaction) -> Unit)? = null
  )

  fun fromStocksPagerToSearch(
    params: Any? = null,
    onTransaction: ((FragmentTransaction) -> Unit)? = null
  )

  fun fromStocksPagerToSettings(
    params: Any? = null,
    onTransaction: ((FragmentTransaction) -> Unit)? = null
  )

  fun fromDefaultStocksToAbout(
    params: Any? = null,
    onTransaction: ((FragmentTransaction) -> Unit)? = null
  )

  fun fromFavouriteStocksToAbout(
    params: Any? = null,
    onTransaction: ((FragmentTransaction) -> Unit)? = null
  )

  fun fromSearchToAbout(
    params: Any? = null,
    onTransaction: ((FragmentTransaction) -> Unit)? = null
  )

  fun fromSettingsToLogin(
    params: Any? = null,
    onTransaction: ((FragmentTransaction) -> Unit)? = null
  )
}