package com.ferelin.stockprice.ui.aboutSection.chart

/*
* Charts Tabs
* */
sealed class ChartViewMode {
    object All : ChartViewMode()
    object Year : ChartViewMode()
    object SixMonths : ChartViewMode()
    object Months : ChartViewMode()
    object Weeks : ChartViewMode()
    object Days : ChartViewMode()
}