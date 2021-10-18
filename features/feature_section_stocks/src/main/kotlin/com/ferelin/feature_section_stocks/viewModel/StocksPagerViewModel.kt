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

package com.ferelin.feature_section_stocks.viewModel

import android.view.View
import androidx.lifecycle.ViewModel
import com.ferelin.navigation.Router
import javax.inject.Inject

class StocksPagerViewModel @Inject constructor(
    private val router: Router
) : ViewModel() {

    var lastSelectedPage = 0

    fun onSettingsClick() {
        router.fromStocksPagerToSettings()
    }

    fun onSearchCardClick(sharedElement: View, name: String) {
        router.fromStocksPagerToSearch { fragmentTransaction ->
            fragmentTransaction.addSharedElement(sharedElement, name)
        }
    }
}