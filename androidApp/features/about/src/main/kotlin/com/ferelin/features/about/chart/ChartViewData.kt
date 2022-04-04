package com.ferelin.features.about.chart

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

internal data class Candles(
  val prices: List<Double> = emptyList(),
  val pricesStr: List<String> = emptyList(),
  val dates: List<String> = emptyList()
)