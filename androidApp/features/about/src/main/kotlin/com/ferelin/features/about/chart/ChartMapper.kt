package com.ferelin.features.about.chart

import com.ferelin.core.domain.entity.PastPrice
import com.ferelin.core.ui.viewData.utils.parseMonthFromDate
import com.ferelin.core.ui.viewData.utils.parseYearFromDate
import com.ferelin.core.ui.viewData.utils.toDateStr
import com.ferelin.core.ui.viewData.utils.toStrPrice

internal object ChartMapper {
  fun map(pastPrice: PastPrice): PastPriceViewData {
    val date = pastPrice.dateMillis.toDateStr()
    return PastPriceViewData(
      closePrice = pastPrice.closePrice,
      closePriceStr = pastPrice.closePrice.toStrPrice(),
      month = parseMonthFromDate(date),
      year = parseYearFromDate(date),
      date = date
    )
  }

  fun mapByViewMode(
    viewMode: ChartViewMode,
    pastPrices: List<PastPriceViewData>
  ): Candles? {
    return when (viewMode) {
      ChartViewMode.All -> mapDays(pastPrices)
      ChartViewMode.Days -> mapDays(pastPrices)
      ChartViewMode.Year -> mapToYear(pastPrices)
      ChartViewMode.SixMonths -> mapToHalfYear(pastPrices)
      ChartViewMode.Months -> mapToMonths(pastPrices)
      ChartViewMode.Weeks -> mapToWeeks(pastPrices)
    }
  }
}

internal fun mapDays(pastPrice: List<PastPriceViewData>): Candles {
  val prices = List(pastPrice.size) { pastPrice[it].closePrice }
  val pricesStr = List(pastPrice.size) { pastPrice[it].closePriceStr }
  val dates = List(pastPrice.size) { pastPrice[it].date }
  return Candles(prices, pricesStr, dates)
}

internal fun mapToYear(pastPrices: List<PastPriceViewData>): Candles? {
  if (pastPrices.size < 2) {
    return null
  }

  val startMonth = pastPrices[0].month
  val startYear = pastPrices[0].year
  val startDate = "$startMonth $startYear"

  val endMonth = pastPrices.last().month
  val endYear = pastPrices.last().year
  val endDate = "$endMonth $endYear"

  return Candles(
    prices = listOf(pastPrices[0].closePrice, pastPrices.last().closePrice),
    pricesStr = listOf(pastPrices[0].closePriceStr, pastPrices.last().closePriceStr),
    dates = listOf(startDate, endDate)
  )
}

internal fun mapToHalfYear(pastPrices: List<PastPriceViewData>): Candles? {
  if (pastPrices.isEmpty()) {
    return null
  }

  val firstHalfBorder = pastPrices.size / 2

  val firstHalfAverage = pastPrices
    .subList(0, firstHalfBorder)
    .sumOf { it.closePrice } / (firstHalfBorder + 1)

  val firstHalfFrom = pastPrices[0].month
  val firstHalfTo = pastPrices[firstHalfBorder].month

  val secondHalfAmount = pastPrices
    .subList(firstHalfBorder + 1, pastPrices.lastIndex)
    .sumOf { it.closePrice }

  val secondHalfAverage = secondHalfAmount / (pastPrices.size - firstHalfBorder + 1)
  val secondHalfFrom = pastPrices[firstHalfBorder + 1].month
  val secondHalfTo = pastPrices[pastPrices.lastIndex].month

  return Candles(
    prices = listOf(firstHalfAverage, secondHalfAverage),
    pricesStr = listOf(
      firstHalfAverage.toStrPrice(),
      secondHalfAverage.toStrPrice()
    ),
    dates = listOf("$firstHalfFrom - $firstHalfTo", "$secondHalfFrom - $secondHalfTo")
  )
}

internal fun mapToMonths(pastPrices: List<PastPriceViewData>): Candles? {
  if (pastPrices.isEmpty()) {
    return null
  }

  val prices = mutableListOf<Double>()
  val pricesStr = mutableListOf<String>()
  val dates = mutableListOf<String>()

  var stepMonth = pastPrices[0].month
  var pastPricesCounter = 0
  var stepAmount = 0.0

  pastPrices.forEach { pastPrice ->
    stepAmount += pastPrice.closePrice
    pastPricesCounter++

    val currentMonth = pastPrice.month

    if (stepMonth != currentMonth) {
      val average = stepAmount / pastPricesCounter

      prices.add(average)
      pricesStr.add(average.toStrPrice())
      dates.add(stepMonth)

      stepMonth = currentMonth
      pastPricesCounter = 0
      stepAmount = 0.0
    }
  }
  return Candles(prices, pricesStr, dates)
}

internal fun mapToWeeks(pastPrices: List<PastPriceViewData>): Candles? {
  if (pastPrices.isEmpty()) {
    return null
  }

  val prices = mutableListOf<Double>()
  val pricesStr = mutableListOf<String>()
  val dates = mutableListOf<String>()

  var pastPricesCounter = 0
  var stepAmount = 0.0
  val daysInWeek = 7

  pastPrices.forEach { pastPrice ->
    stepAmount += pastPrice.closePrice
    pastPricesCounter++

    if (pastPricesCounter == daysInWeek) {
      val average = stepAmount / daysInWeek

      prices.add(average)
      pricesStr.add(average.toStrPrice())
      dates.add(pastPrice.date)

      stepAmount = 0.0
      pastPricesCounter = 0
    }
  }
  return Candles(prices, pricesStr, dates)
}