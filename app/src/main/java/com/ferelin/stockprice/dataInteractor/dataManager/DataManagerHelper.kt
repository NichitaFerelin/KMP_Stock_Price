package com.ferelin.stockprice.dataInteractor.dataManager

object DataManagerHelper {

    fun calculateProfit(currentPrice: Double, previousPrice: Double): String {
        val numberProfit = currentPrice - previousPrice
        val numberProfitStr = numberProfit.toString()

        val digitNumberProfit = numberProfitStr.substringBefore('.').filter { it.isDigit() }
        val remainderNumberProfit = with(numberProfitStr.substringAfter('.')) {
            if (length >= 2) substring(0, 2) else this
        }

        val percentProfit = (100 * (currentPrice - previousPrice) / currentPrice).toString()
        val digitPercentProfit = percentProfit.substringBefore('.').filter { it.isDigit() }
        val remainderPercentProfit = with(percentProfit.substringAfter('.')) {
            if (length >= 2) substring(0, 2) else this
        }

        val prefix = if (currentPrice > previousPrice) "+" else "-"
        return "$prefix$$digitNumberProfit.$remainderNumberProfit ($digitPercentProfit,$remainderPercentProfit%)"
    }
}