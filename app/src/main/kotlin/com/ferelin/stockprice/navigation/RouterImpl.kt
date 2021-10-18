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

import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import com.ferelin.navigation.Router
import com.ferelin.navigation.ScreenResolver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RouterImpl @Inject constructor(
    private val screenResolver: ScreenResolver
) : Router {

    private var _activity: FragmentActivity? = null
    private val activity: FragmentActivity
        get() = checkNotNull(_activity)

    override fun bind(activity: FragmentActivity) {
        this._activity = activity
    }

    override fun unbind() {
        _activity = null
    }

    override fun back() {
        activity.supportFragmentManager.popBackStack()
    }

    override fun toStartFragment() {
        screenResolver.toLoadingFragment(activity)
    }

    override fun fromLoadingToStocksPager(
        params: Any?,
        onTransaction: ((FragmentTransaction) -> Unit)?
    ) {
        screenResolver.fromLoadingToStocksPager(activity, params, onTransaction)
    }

    override fun fromStocksPagerToSearch(
        params: Any?,
        onTransaction: ((FragmentTransaction) -> Unit)?
    ) {
        screenResolver.fromStocksPagerToSearch(activity, params, onTransaction)
    }

    override fun fromStocksPagerToSettings(
        params: Any?,
        onTransaction: ((FragmentTransaction) -> Unit)?
    ) {
        screenResolver.fromStocksPagerToSettings(activity, params, onTransaction)
    }

    override fun fromDefaultStocksToAbout(
        params: Any?,
        onTransaction: ((FragmentTransaction) -> Unit)?
    ) {
        screenResolver.fromDefaultStocksToAbout(activity, params, onTransaction)
    }

    override fun fromFavouriteStocksToAbout(
        params: Any?,
        onTransaction: ((FragmentTransaction) -> Unit)?
    ) {
        screenResolver.fromFavouriteStocksToAbout(activity, params, onTransaction)
    }

    override fun fromSearchToAbout(
        params: Any?,
        onTransaction: ((FragmentTransaction) -> Unit)?
    ) {
        screenResolver.fromSearchToAbout(activity, params, onTransaction)
    }

    override fun fromSettingsToLogin(
        params: Any?,
        onTransaction: ((FragmentTransaction) -> Unit)?
    ) {
        screenResolver.fromSettingsToLogin(activity, params, onTransaction)
    }
}