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

package com.ferelin.core.utils

import android.content.Context
import android.content.res.Configuration
import android.content.res.Resources
import android.view.View
import androidx.constraintlayout.motion.widget.MotionLayout

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val Context.isLandscapeOrientation: Boolean
    get() = this.resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE

val MotionLayout.isAtEnd: Boolean
    get() = progress == 1F

var View.isOut: Boolean
    get() = scaleX == 0F
    set(value) {
        if (value) {
            scaleX = 0F
            scaleY = 0F
        } else {
            scaleX = 1F
            scaleY = 1F
        }
    }

inline fun <T> List<T>.ifNotEmpty(defaultValue: (data: List<T>) -> Unit) {
    if (this.isNotEmpty()) {
        defaultValue.invoke(this)
    }
}

fun View.setOnClick(listener: (() -> Unit)) {
    setOnClickListener { listener.invoke() }
}