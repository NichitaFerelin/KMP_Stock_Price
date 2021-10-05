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

package com.ferelin.stockprice.navigation

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import com.ferelin.feature_loading.view.LoadingFragment
import com.ferelin.feature_search.view.SearchFragment
import com.ferelin.feature_section_about.view.AboutPagerFragment
import com.ferelin.feature_section_stocks.view.StocksPagerFragment
import com.ferelin.navigation.ScreenResolver
import com.ferelin.stockprice.R
import javax.inject.Inject

class ScreenResolverImpl @Inject constructor() : ScreenResolver {

    override fun toLoadingFragment(hostActivity: FragmentActivity) {
        replaceMainContainerBy(
            hostActivity.supportFragmentManager,
            LoadingFragment.newInstance(null),
            ScreenResolver.LOADING_TAG,
            false
        )
    }

    override fun fromLoadingToStocksPager(hostActivity: FragmentActivity, data: Any?) {
        replaceMainContainerBy(
            hostActivity.supportFragmentManager,
            StocksPagerFragment.newInstance(data),
            ScreenResolver.STOCKS_PAGER_TAG,
            false
        )
    }

    override fun fromStocksPagerToSearch(hostActivity: FragmentActivity, data: Any?) {
        replaceMainContainerBy(
            hostActivity.supportFragmentManager,
            SearchFragment.newInstance(data),
            ScreenResolver.SEARCH_TAG
        )
    }

    override fun fromDefaultStocksToAbout(hostActivity: FragmentActivity, data: Any?) {
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
            AboutPagerFragment.newInstance(data),
            ScreenResolver.ABOUT_PAGER_TAG
        )
    }

    override fun fromFavouriteStocksToAbout(hostActivity: FragmentActivity, data: Any?) {
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
            AboutPagerFragment.newInstance(data),
            ScreenResolver.ABOUT_PAGER_TAG
        )
    }

    override fun fromSearchToAbout(hostActivity: FragmentActivity, data: Any?) {
        val parentManager = hostActivity
            .supportFragmentManager
            .findFragmentByTag(ScreenResolver.SEARCH_TAG)
            ?.parentFragmentManager
            ?: throw IllegalStateException("Cannot find fragment by tag ${ScreenResolver.SEARCH_TAG}")

        replaceMainContainerBy(
            parentManager,
            SearchFragment.newInstance(data),
            ScreenResolver.ABOUT_PAGER_TAG
        )
    }

    private fun replaceMainContainerBy(
        fragmentManager: FragmentManager,
        fragment: Fragment,
        tag: String,
        addToBackStack: Boolean = true
    ) {
        fragmentManager.commit {
            setReorderingAllowed(true)
            replace(R.id.container, fragment, tag)
            if (addToBackStack) addToBackStack(null)
        }
    }
}