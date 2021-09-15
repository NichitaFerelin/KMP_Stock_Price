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

package com.ferelin.stockprice.utils

import android.animation.Animator
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.view.animation.Animation
import androidx.annotation.AttrRes
import androidx.core.content.res.use

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Context.themeColor(@AttrRes themeAttrId: Int): Int {
    return obtainStyledAttributes(intArrayOf(themeAttrId))
        .use { it.getColor(0, Color.MAGENTA) }
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

fun Animation.invalidate() {
    setAnimationListener(null)
    cancel()
}

fun Animator.invalidate() {
    removeAllListeners()
    cancel()
}