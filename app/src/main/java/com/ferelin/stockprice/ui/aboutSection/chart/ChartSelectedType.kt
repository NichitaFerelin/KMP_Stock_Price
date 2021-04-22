package com.ferelin.stockprice.ui.aboutSection.chart

/*
* Charts Tabs
* */
sealed class ChartSelectedType {
    object All : ChartSelectedType()
    object Year : ChartSelectedType()
    object SixMonths : ChartSelectedType()
    object Months : ChartSelectedType()
    object Weeks : ChartSelectedType()
    object Days : ChartSelectedType()
}