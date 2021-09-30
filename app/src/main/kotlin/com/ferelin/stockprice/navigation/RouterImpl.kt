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
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import androidx.navigation.NavDirections
import androidx.navigation.findNavController
import com.ferelin.navigation.Router
import com.ferelin.navigation.ScreenResolver
import com.ferelin.stockprice.R
import com.ferelin.stockprice.ui.MainActivity
import javax.inject.Inject

class RouterImpl @Inject constructor(
    private val mScreenResolver: ScreenResolver
) : Router {

    private var activity: FragmentActivity? = null
    private val mActivity: FragmentActivity
        get() = checkNotNull(activity)

    override fun bind(activity: FragmentActivity) {
        this.activity = activity
    }

    override fun unbind() {
        activity = null
    }

    override fun back() {
    }

    override fun fromLoadingToWelcome(args: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun fromLoadingToStocksPager(args: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun fromStocksPagerToSearch(args: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun fromDefaultStocksToAbout(args: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun fromFavouriteStocksToAbout(args: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun fromSearchToAbout(args: Bundle?) {
        TODO("Not yet implemented")
    }
}