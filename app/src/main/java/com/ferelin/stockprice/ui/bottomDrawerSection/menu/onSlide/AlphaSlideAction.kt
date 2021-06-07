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

package com.ferelin.stockprice.ui.bottomDrawerSection.menu.onSlide

import android.view.View
import com.ferelin.stockprice.utils.bottomDrawer.OnSlideAction

/**
 * Change the alpha of [view] when a bottom sheet is slid.
 *
 * @param reverse Setting reverse to true will cause the view's alpha to approach 0.0 as the sheet
 *  slides up. The default behavior, false, causes the view's alpha to approach 1.0 as the sheet
 *  slides up.
 */
class AlphaSlideAction(
    private val view: View,
    private val reverse: Boolean = false
) : OnSlideAction {

    override fun onSlide(sheet: View, slideOffset: Float) {
        val alpha = slideOffset.normalize(-1F, 0F, 0F, 1F)
        view.alpha = if (!reverse) alpha else 1F - alpha
        if (view.alpha == 0F && view.visibility == View.VISIBLE) {
            view.visibility = View.GONE
        } else if (view.alpha != 0F && view.visibility == View.GONE) {
            view.visibility = View.VISIBLE
        }
    }
}

fun Float.normalize(
    inputMin: Float,
    inputMax: Float,
    outputMin: Float,
    outputMax: Float
): Float {
    if (this < inputMin) {
        return outputMin
    } else if (this > inputMax) {
        return outputMax
    }

    return outputMin * (1 - (this - inputMin) / (inputMax - inputMin)) +
            outputMax * ((this - inputMin) / (inputMax - inputMin))
}