package com.ferelin.core.ui.view.custom.chart.utils

import android.graphics.LinearGradient
import android.graphics.Paint
import android.graphics.Path
import androidx.annotation.ColorInt
import com.ferelin.core.ui.view.px

internal class ChartAttrs(
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