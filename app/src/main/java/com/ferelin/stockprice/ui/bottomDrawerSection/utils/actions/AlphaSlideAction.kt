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

package com.ferelin.stockprice.ui.bottomDrawerSection.utils.actions

import android.view.View
import androidx.core.view.isVisible
import com.ferelin.stockprice.utils.bottomDrawer.OnSlideAction
import com.ferelin.stockprice.utils.normalize

/**
 * Changes the alpha of [view] when a bottom sheet is slid.
 */
class AlphaSlideAction(
    private val view: View,
    private val reverse: Boolean = false
) : OnSlideAction {

    override fun onSlide(sheet: View, slideOffset: Float) {
        val alpha = slideOffset.normalize(-1F, 0F, 0F, 1F)
        view.alpha = if (!reverse) alpha else 1F - alpha

        if (view.alpha == 0F && view.isVisible) {
            view.isVisible = false
        } else if (view.alpha != 0F && !view.isVisible) {
            view.isVisible = true
        }
    }
}