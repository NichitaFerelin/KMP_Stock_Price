package com.ferelin.stockprice.utils

import android.content.res.Resources
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import java.util.*

fun search(item: AdaptiveCompany, text: String): Boolean {
    return item.name.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))
            || item.symbol.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))
}

val Int.dp: Int
    get() = (this / Resources.getSystem().displayMetrics.density).toInt()

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()