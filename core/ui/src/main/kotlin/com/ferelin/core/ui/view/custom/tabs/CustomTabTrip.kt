package com.ferelin.core.ui.view.custom.tabs

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.animation.AccelerateInterpolator
import android.view.animation.DecelerateInterpolator
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import com.ferelin.core.ui.R

internal class CustomTabTrip(
  context: Context,
  attrs: AttributeSet?
) : LinearLayout(context) {
  init {
    setWillNotDraw(false)
  }

  private val rect = RectF()
  private val paint = Paint(Paint.ANTI_ALIAS_FLAG)
  private val cornerRadius = CORNER_RADIUS * resources.displayMetrics.density

  private var lastPosition = 0
  private var selectedPosition = 0
  private var selectionOffset = 0f

  private val leftInterpolator = AccelerateInterpolator(INTERPOLATOR_FACTOR)
  private val rightInterpolator = DecelerateInterpolator(INTERPOLATOR_FACTOR)

  override fun onDraw(canvas: Canvas) {
    drawDecoration(canvas)
  }

  fun onViewPagerPageChanged(position: Int, positionOffset: Float) {
    selectedPosition = position
    selectionOffset = positionOffset

    if (positionOffset == 0f && lastPosition != selectedPosition) {
      lastPosition = selectedPosition
    }

    invalidate()
  }

  private fun drawDecoration(canvas: Canvas) {
    val isLayoutRtl = CustomTabUtils.isLayoutRtl(this)

    if (childCount > 0) {
      val selectedTab = getChildAt(selectedPosition)
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

      var thickness = THICKNESS.toFloat()

      if (selectionOffset > 0f && selectedPosition < childCount - 1) {
        val startOffset = leftInterpolator.getInterpolation(selectionOffset)
        val endOffset = rightInterpolator.getInterpolation(selectionOffset)
        val thicknessOffset = 1f / (1.0f - startOffset + endOffset)

        val nextTab = getChildAt(selectedPosition + 1)
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
    val center = height - THICKNESS / 2f
    val top = center - thickness / 2f
    val bottom = center + thickness / 2f

    paint.color = color
    rect[left.toFloat(), top, right.toFloat()] = bottom

    canvas.drawRoundRect(rect, cornerRadius, cornerRadius, paint)
  }

  private companion object {
    const val CORNER_RADIUS = 8f
    const val THICKNESS = 10
    const val INTERPOLATOR_FACTOR = 3.0F
  }
}