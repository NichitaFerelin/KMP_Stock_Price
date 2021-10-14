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

package com.ferelin.navigation

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction

interface ScreenResolver {

    companion object {
        const val STOCKS_PAGER_TAG = "stocks-pager"
        const val SEARCH_TAG = "search"
        const val ABOUT_PAGER_TAG = "about"
        const val DEFAULT_STOCKS_TAG = "stocks"
        const val FAVOURITE_STOCKS_TAG = "favourite-stocks"
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
}