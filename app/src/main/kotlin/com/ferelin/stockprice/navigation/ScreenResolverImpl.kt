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

import android.os.Bundle
import androidx.navigation.NavController
import com.ferelin.navigation.ScreenResolver
import com.ferelin.stockprice.R
import javax.inject.Inject

class ScreenResolverImpl @Inject constructor() : ScreenResolver {

    override fun fromLoadingToWelcome(navController: NavController) {
        navController.navigate(
            R.id.action_loadingFragment_to_welcomeFragment
        )
    }

    override fun fromLoadingToStocksPager(navController: NavController) {
        navController.navigate(
            R.id.action_loadingFragment_to_stocksPagerFragment
        )
    }

    override fun fromStocksPagerToSearch(navController: NavController) {
        navController.navigate(
            R.id.action_stocksPagerFragment_to_searchFragment
        )
    }

    override fun fromDefaultStocksToAbout(navController: NavController, args: Bundle) {
        navController.navigate(
            R.id.action_stocksFragment_to_aboutPagerFragment,
            args
        )
    }

    override fun fromFavouriteStocksToAbout(navController: NavController, args: Bundle) {
        navController.navigate(
            R.id.action_favouriteFragment_to_aboutPagerFragment,
            args
        )
    }
}