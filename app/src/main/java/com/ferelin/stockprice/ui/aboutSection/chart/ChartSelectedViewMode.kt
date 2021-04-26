package com.ferelin.stockprice.ui.aboutSection.chart

/*
* Charts Tabs
* */
sealed class ChartSelectedViewMode {
    object All : ChartSelectedViewMode()
    object Year : ChartSelectedViewMode()
    object SixMonths : ChartSelectedViewMode()
    object Months : ChartSelectedViewMode()
    object Weeks : ChartSelectedViewMode()
    object Days : ChartSelectedViewMode()
}