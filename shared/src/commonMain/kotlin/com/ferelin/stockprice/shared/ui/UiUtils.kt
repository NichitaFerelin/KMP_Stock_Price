package com.ferelin.stockprice.shared.ui

import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.SimpleDateFormat
import java.util.*

internal const val DATE_PATTERN = "dd MMM yyyy"
internal const val PRICE_PATTERN = "###,##0.00"
internal const val PRICE_SPLIT_SYMBOL = '.'

fun Long.toDateStr(): String {
    val dateFormat = SimpleDateFormat(DATE_PATTERN, Locale.ROOT)
    return dateFormat.format(Date(this)).filter { it != ',' }
}

/*
* Builds price string from double
* Call:     adaptPrice(2253.14)
* Result:   $2 253.14
* */
fun Double.toStrPrice(): String {
    val balanceFormatSymbols = DecimalFormatSymbols().apply {
        groupingSeparator = ' '
        decimalSeparator = PRICE_SPLIT_SYMBOL
    }
    val formattedBalance = DecimalFormat(PRICE_PATTERN, balanceFormatSymbols).format(this)
    return "$$formattedBalance"
}

/*
* buildProfitString (100.0, 50.0) = "+$50.0 (50,0%)"
* */
fun buildProfitString(currentPrice: Double, previousPrice: Double): String {
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

/**
 * formatProfitString(
 *      priceProfit = "3738.94748833",
 *      priceProfitPercents = "0.0593"
 *  ) = "+$3 738.94 (0,05%)"
 * */
fun formatProfitString(priceProfit: String, priceProfitPercents: String): String {
    if (
        priceProfit.length < 2
        || priceProfitPercents.length < 2
    ) return ""

    // If the profit is negative, then at the beginning there should be '-'
    // otherwise there will be the beginning of the number
    val prefix = if (priceProfit[0].isDigit()) {
        "+"
    } else "-"

    val profitStartIndex = if (prefix == "+") 0 else 1
    val profitResult = prefix + priceProfit.substring(profitStartIndex).toDouble().toStrPrice()

    val percents = priceProfitPercents.substring(profitStartIndex)
    val mainPart = percents.substringBefore(".")

    val remainder = priceProfitPercents.substringAfter('.', "")
    val remainderResult = if (remainder.length > 2) {
        remainder.substring(0, 2)
    } else remainder

    val secondPart = if (remainderResult.isEmpty()) {
        ""
    } else ",$remainderResult"
    return "$profitResult ($mainPart$secondPart%)"
}