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
import androidx.fragment.app.FragmentActivity
import androidx.navigation.NavController
import com.ferelin.navigation.ScreenResolver
import com.ferelin.stockprice.R
import javax.inject.Inject

class ScreenResolverImpl @Inject constructor() : ScreenResolver {

    override fun fromLoadingToWelcome(hostActivity: FragmentActivity, args: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun fromLoadingToStocksPager(hostActivity: FragmentActivity, args: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun fromStocksPagerToSearch(hostActivity: FragmentActivity, args: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun fromDefaultStocksToAbout(hostActivity: FragmentActivity, args: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun fromFavouriteStocksToAbout(hostActivity: FragmentActivity, args: Bundle?) {
        TODO("Not yet implemented")
    }

    override fun fromSearchToAbout(hostActivity: FragmentActivity, args: Bundle?) {
        TODO("Not yet implemented")
    }
}