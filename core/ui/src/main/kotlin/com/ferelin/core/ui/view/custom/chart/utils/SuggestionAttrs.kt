package com.ferelin.core.ui.view.custom.chart.utils

import android.content.Context
import android.graphics.Paint
import androidx.annotation.ColorInt
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import com.ferelin.core.ui.R

internal class SuggestionAttrs(
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

  val mainPointRadius = context.resources.getDimension(R.dimen.chartPointSize)
  val mainPointPaint = Paint().apply {
    style = Paint.Style.FILL
    isAntiAlias = true
    color = ContextCompat.getColor(context, R.color.white)
  }

  val subPointRadius = context.resources.getDimension(R.dimen.chartPointSubSize)
  val subPointPaint = Paint().apply {
    style = Paint.Style.FILL
    isAntiAlias = true
    color = ContextCompat.getColor(context, R.color.black)
  }
}