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

package com.ferelin.stockprice.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.FrameLayout
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import com.ferelin.stockprice.R
import com.ferelin.stockprice.ui.dialogs.DialogErrorFragment
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.STATE_HIDDEN
import java.util.*
import kotlin.concurrent.timerTask

const val NULL_INDEX = -1

fun showDialog(text: String, fragmentManager: FragmentManager) {
    DialogErrorFragment
        .newInstance(text)
        .show(fragmentManager, null)
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

fun showDefaultDialog(context: Context, message: String) {
    AlertDialog.Builder(context)
        .setMessage(message)
        .setCancelable(true)
        .setPositiveButton(R.string.hintOk) { dialog, _ -> dialog.cancel() }
        .show()
}

fun BottomSheetBehavior<FrameLayout>.isHidden(): Boolean {
    return state == STATE_HIDDEN
}