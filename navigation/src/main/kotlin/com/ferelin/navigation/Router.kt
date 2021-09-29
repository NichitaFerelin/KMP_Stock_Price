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

import android.app.Activity
import android.os.Bundle
import androidx.navigation.NavDirections

interface Router {

    fun bind(activity: Activity)

    fun unbind()

    fun back()

    fun fromLoadingToWelcome()

    fun fromLoadingToStocksPager()

    fun fromStocksPagerToSearch(navDirections: NavDirections)

    fun fromDefaultStocksToAbout(navDirections: NavDirections, args: Bundle)

    fun fromFavouriteStocksToAbout(navDirections: NavDirections, args: Bundle)
}