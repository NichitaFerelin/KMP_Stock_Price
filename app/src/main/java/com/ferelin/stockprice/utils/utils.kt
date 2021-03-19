package com.ferelin.stockprice.utils

import android.content.res.Resources
import android.view.View
import androidx.fragment.app.FragmentManager
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.ui.dialogs.DialogErrorFragment
import com.google.android.material.snackbar.Snackbar
import java.util.*

const val NULL_INDEX = -1

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun filterCompanies(item: AdaptiveCompany, text: String): Boolean {
    return item.companyProfile.name.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))
            || item.companyProfile.symbol.toLowerCase(Locale.ROOT)
        .contains(text.toLowerCase(Locale.ROOT))
}

fun parseDoubleFromStr(str: String): Double {
    return str.filter { it.isDigit() || it == '.' }.toDoubleOrNull() ?: 0.0
}

fun showSnackbar(view: View, text: String) {
    val snackbar = Snackbar.make(view, text, Snackbar.LENGTH_INDEFINITE)
    snackbar.setAction("OK") { snackbar.dismiss() }.show()
}

fun showDialog(text: String, fragmentManager: FragmentManager) {
    DialogErrorFragment
        .newInstance(text)
        .show(fragmentManager, null)
}