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

package com.ferelin.core.view.tabs

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.ferelin.core.R

internal class CustomTabTrip(context: Context, attrs: AttributeSet?) : LinearLayout(context) {

    init {
        setWillNotDraw(false)
    }

    private val mRect = RectF()
    private val mPaint = Paint(Paint.ANTI_ALIAS_FLAG)
    private val mCornerRadius = sDefaultIndicatorCornerRadius * resources.displayMetrics.density

    private var mLastPosition = 0
    private var mSelectedPosition = 0
    private var mSelectionOffset = 0f

    private val mLeftInterpolator = AccelerateInterpolator(sDefaultInterpolatorFactor)
    private val mRightInterpolator = DecelerateInterpolator(sDefaultInterpolatorFactor)

    override fun onDraw(canvas: Canvas) {
        drawDecoration(canvas)
    }

    fun onViewPagerPageChanged(position: Int, positionOffset: Float) {
        mSelectedPosition = position
        mSelectionOffset = positionOffset

        if (positionOffset == 0f && mLastPosition != mSelectedPosition) {
            mLastPosition = mSelectedPosition
        }

        invalidate()
    }

    private fun drawDecoration(canvas: Canvas) {
        val isLayoutRtl = CustomTabUtils.isLayoutRtl(this)

        if (childCount > 0) {
            val selectedTab = getChildAt(mSelectedPosition)
            val selectedStart = CustomTabUtils.getStart(selectedTab)
            val selectedEnd = CustomTabUtils.getEnd(selectedTab)

            var left: Int
            var right: Int

            if (isLayoutRtl) {
                left = selectedEnd
                right = selectedStart
            } else {
                left = selectedStart
                right = selectedEnd
            }

            var thickness = sDefaultThickness.toFloat()

            if (mSelectionOffset > 0f && mSelectedPosition < childCount - 1) {
                val startOffset = mLeftInterpolator.getInterpolation(mSelectionOffset)
                val endOffset = mRightInterpolator.getInterpolation(mSelectionOffset)
                val thicknessOffset = 1f / (1.0f - startOffset + endOffset)

                val nextTab = getChildAt(mSelectedPosition + 1)
                val nextStart = CustomTabUtils.getStart(nextTab)
                val nextEnd = CustomTabUtils.getEnd(nextTab)

                if (isLayoutRtl) {
                    left = (endOffset * nextEnd + (1.0f - endOffset) * left).toInt()
                    right = (startOffset * nextStart + (1.0f - startOffset) * right).toInt()
                } else {
                    left = (startOffset * nextStart + (1.0f - startOffset) * left).toInt()
                    right = (endOffset * nextEnd + (1.0f - endOffset) * right).toInt()
                }

                thickness *= thicknessOffset
            }
            drawIndicator(
                canvas,
                left,
                right,
                height,
                thickness,
                ContextCompat.getColor(context, R.color.black)
            )
        }
    }

    private fun drawIndicator(
        canvas: Canvas, left: Int, right: Int, height: Int, thickness: Float,
        color: Int
    ) {
        val center = height - sDefaultThickness / 2f
        val top = center - thickness / 2f
        val bottom = center + thickness / 2f

        mPaint.color = color
        mRect[left.toFloat(), top, right.toFloat()] = bottom

        canvas.drawRoundRect(mRect, mCornerRadius, mCornerRadius, mPaint)
    }

    private companion object {
        const val sDefaultIndicatorCornerRadius = 8f
        const val sDefaultThickness = 10
        const val sDefaultInterpolatorFactor = 3.0F
    }
}