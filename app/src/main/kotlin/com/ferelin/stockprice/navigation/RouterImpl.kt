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
import com.ferelin.navigation.Router
import com.ferelin.navigation.ScreenResolver
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
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
        mActivity.supportFragmentManager.popBackStack()
    }

    override fun toStartFragment() {
        mScreenResolver.toLoadingFragment(mActivity)
    }

    override fun fromLoadingToStocksPager(data: Any?) {
        mScreenResolver.fromLoadingToStocksPager(mActivity, data)
    }

    override fun fromStocksPagerToSearch(data: Any?) {
        mScreenResolver.fromStocksPagerToSearch(mActivity, data)
    }

    override fun fromDefaultStocksToAbout(data: Any?) {
        mScreenResolver.fromDefaultStocksToAbout(mActivity, data)
    }

    override fun fromFavouriteStocksToAbout(data: Any?) {
        mScreenResolver.fromFavouriteStocksToAbout(mActivity, data)
    }

    override fun fromSearchToAbout(data: Any?) {
        mScreenResolver.fromSearchToAbout(mActivity, data)
    }
}