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

import android.content.Context
import android.graphics.Paint
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.ferelin.core.R

class SuggestionAttrs(
    context: Context,
    val suggestionWidth: Float,
    val suggestionHeight: Float,
    val suggestionRectRadius: Float,
    @ColorInt suggestionBackgroundColor: Int,
    @ColorInt suggestionPriceColor: Int,
    @ColorInt suggestionDateColor: Int
) {

    /**
     * Paint for main suggestion board on whic prices will be drawn
     * */
    val boardPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        color = suggestionBackgroundColor
    }

    val suggestionMarginBetween = context.resources.getDimension(R.dimen.suggestionMarginBetween)
    val offsetFromPoint = context.resources.getDimension(R.dimen.suggestionOffsetFromPoint)

    val pricePaint = Paint().apply {
        typeface = ResourcesCompat.getFont(context, R.font.w_600)
        color = suggestionPriceColor
        textSize = context.resources.getDimension(R.dimen.textViewBody)
    }

    val datePaint = Paint().apply {
        typeface = ResourcesCompat.getFont(context, R.font.w_600)
        color = suggestionDateColor
        textSize = context.resources.getDimension(R.dimen.textViewBody)
    }

    val mainPointRadius = context.resources.getDimension(R.dimen.pointWhiteRadius)
    val mainPointPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.white)
    }

    val subPointRadius = context.resources.getDimension(R.dimen.pointBlackRadius)
    val subPointPaint = Paint().apply {
        style = Paint.Style.FILL
        isAntiAlias = true
        color = ContextCompat.getColor(context, R.color.black)
    }
}