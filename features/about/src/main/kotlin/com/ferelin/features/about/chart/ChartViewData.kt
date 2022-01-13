package com.ferelin.features.about.chart

enum class ChartViewMode {
  All,
  Year,
  SixMonths,
  Months,
  Weeks,
  Days
}

data class PastPriceViewData(
  val closePrice: Double,
  val closePriceStr: String,
  val month: String,
  val year: String,
  val date: String
)

data class ChartPastPrices(
  val prices: List<Double> = emptyList(),
  val pricesStr: List<String> = emptyList(),
  val dates: List<String> = emptyList()
)