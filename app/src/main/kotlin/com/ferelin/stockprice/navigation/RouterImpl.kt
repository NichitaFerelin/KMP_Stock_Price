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

import android.app.Activity
import android.os.Bundle
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.ferelin.navigation.Router
import com.ferelin.navigation.ScreenResolver
import com.ferelin.stockprice.R
import javax.inject.Inject

class RouterImpl @Inject constructor(
    private val mScreenResolver: ScreenResolver
) : Router {

    private var activity: Activity? = null
    private val mActivity: Activity
        get() = checkNotNull(activity)

    private var navController: NavController? = null
    private val mNavController: NavController
        get() = checkNotNull(navController)

    override fun bind(activity: Activity) {
        this.activity = activity
        this.navController = mActivity.findNavController(R.id.container)
    }

    override fun unbind() {
        activity = null
        navController = null
    }

    override fun back() {
        mNavController.navigateUp()
    }

    override fun fromLoadingToWelcome() {
        mScreenResolver.fromLoadingToWelcome(mNavController)
    }

    override fun fromLoadingToStocksPager() {
        mScreenResolver.fromLoadingToStocksPager(mNavController)
    }

    override fun fromStocksPagerToSearch(navDirections: NavDirections) {
        mScreenResolver.fromStocksPagerToSearch(mNavController)
    }

    override fun fromDefaultStocksToAbout(navDirections: NavDirections, args: Bundle) {
        mScreenResolver.fromDefaultStocksToAbout(mNavController, args)
    }

    override fun fromFavouriteStocksToAbout(navDirections: NavDirections, args: Bundle) {
        mScreenResolver.fromFavouriteStocksToAbout(mNavController, args)
    }
}