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

import androidx.fragment.app.*
import com.ferelin.feature_loading.view.LoadingFragment
import com.ferelin.feature_search.view.SearchFragment
import com.ferelin.feature_section_about.view.AboutPagerFragment
import com.ferelin.feature_section_stocks.view.StocksPagerFragment
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
            StocksPagerFragment.newInstance(params),
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
            SearchFragment.newInstance(params),
            ScreenResolver.SEARCH_TAG,
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
            AboutPagerFragment.newInstance(params),
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
            AboutPagerFragment.newInstance(params),
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