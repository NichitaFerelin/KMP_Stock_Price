package com.ferelin.features.about.ui.chart

internal enum class ChartViewMode {
  All,
  Year,
  SixMonths,
  Months,
  Weeks,
  Days
}

internal data class PastPriceViewData(
  val closePrice: Double,
  val closePriceStr: String,
  val month: String,
  val year: String,
  val date: String
)