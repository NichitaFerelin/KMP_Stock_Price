package com.ferelin.stockprice.utils

import android.content.Context
import android.content.res.Resources
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.FragmentManager
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.ui.dialogs.DialogErrorFragment
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

fun showDialog(text: String, fragmentManager: FragmentManager) {
    DialogErrorFragment
        .newInstance(text)
        .show(fragmentManager, null)
}

fun findCompany(data: List<AdaptiveCompany>, symbol: String?): AdaptiveCompany? {
    return data.find { it.companyProfile.symbol == symbol }
}

fun getString(context: Context, resource: Int): String {
    return context.resources.getString(resource)
}

fun openKeyboard(context: Context, view: View) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
}

fun hideKeyboard(context: Context, view: View) {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}