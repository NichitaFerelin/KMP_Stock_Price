package com.ferelin.core.ui.viewData.utils

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