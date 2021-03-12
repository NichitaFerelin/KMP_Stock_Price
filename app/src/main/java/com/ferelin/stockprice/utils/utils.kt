package com.ferelin.stockprice.utils

import android.content.res.Resources
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import java.util.*

const val NULL_INDEX = -1

fun search(item: AdaptiveCompany, text: String): Boolean {
    return item.companyProfile.name.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))
            || item.companyProfile.symbol.toLowerCase(Locale.ROOT)
        .contains(text.toLowerCase(Locale.ROOT))
}

fun parseDoubleFromStr(str: String): Double {
    return str.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()