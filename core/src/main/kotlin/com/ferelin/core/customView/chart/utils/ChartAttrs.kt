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

package com.ferelin.core.customView.chart.utils

import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import androidx.annotation.ColorInt
import com.ferelin.core.utils.px

class ChartAttrs(
    @ColorInt gradientColorStart: Int,
    @ColorInt gradientColorEnd: Int,
    @ColorInt lineColor: Int
) {

    /**
     * Chart background color as gradient
     * */
    val gradientColors = intArrayOf(gradientColorEnd, gradientColorStart)
    var gradient: LinearGradient? = null
    var gradientZeroY: Float = 0F
    val gradientPath: Path = Path()
    var gradientPaint: Paint? = null

    /**
     * Paint for chart points line
     * */
    val linePaint = Paint().apply {
        style = Paint.Style.STROKE
        strokeWidth = 2.px.toFloat()
        isAntiAlias = true
        color = lineColor
    }
    val linePath: Path = Path()
}