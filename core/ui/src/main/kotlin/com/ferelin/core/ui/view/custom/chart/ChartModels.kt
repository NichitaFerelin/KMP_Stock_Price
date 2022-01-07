package com.ferelin.core.ui.view.custom.chart

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

/**
 * [BezierPoint] represents model to build chart
 */
internal data class BezierPoint(
  val x1: Float,
  val y1: Float,
  val x2: Float,
  val y2: Float
)

/**
 * [Point] represents base point with coords for chart
 * */
@Parcelize
internal data class Point(var x: Float, var y: Float) : Parcelable

/**
 * [Marker] represents model of chart "advanced" point with data.
 * */
@Parcelize
internal data class Marker(
  val position: Point = Point(0f, 0f),
  val price: Double,
  val priceStr: String,
  val date: String
) : Parcelable

/**
 * Chart model
 * */
data class ChartPastPrices(
  val prices: List<Double>,
  val pricesStr: List<String>,
  val dates: List<String>
)