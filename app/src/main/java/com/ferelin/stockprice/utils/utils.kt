package com.ferelin.stockprice.utils

/*
 * Copyright 2021 Leah Nichita
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.Resources
import android.graphics.Color
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.core.content.res.use
import androidx.fragment.app.FragmentManager
import com.ferelin.repository.adaptiveModels.AdaptiveCompany
import com.ferelin.stockprice.ui.dialogs.DialogErrorFragment
import java.util.*
import kotlin.concurrent.timerTask

const val NULL_INDEX = -1

val Int.px: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

val View.isOut
    get() = scaleX == 0F

val View.isVisible
    get() = visibility == View.VISIBLE

fun filterCompanies(item: AdaptiveCompany, text: String): Boolean {
    return item.companyProfile.name.toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))
            || item.companyProfile.symbol.toLowerCase(Locale.ROOT)
        .contains(text.toLowerCase(Locale.ROOT))
}

fun showToast(context: Context, text: String) {
    Toast.makeText(context, text, Toast.LENGTH_LONG).show()
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

fun withTimer(time: Long = 200L, body: () -> Unit) {
    Timer().schedule(timerTask {
        body.invoke()
    }, time)
}

@SuppressLint("Recycle")
fun Context.themeColor(
    @AttrRes themeAttrId: Int
): Int {
    return obtainStyledAttributes(
        intArrayOf(themeAttrId)
    ).use {
        it.getColor(0, Color.MAGENTA)
    }
}

fun Float.normalize(
    inputMin: Float,
    inputMax: Float,
    outputMin: Float,
    outputMax: Float
): Float {
    if (this < inputMin) {
        return outputMin
    } else if (this > inputMax) {
        return outputMax
    }

    return outputMin * (1 - (this - inputMin) / (inputMax - inputMin)) +
            outputMax * ((this - inputMin) / (inputMax - inputMin))
}