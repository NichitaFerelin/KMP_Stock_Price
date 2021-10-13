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

package com.ferelin.core.utils

import android.content.Context
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.FragmentManager
import com.ferelin.core.R
import com.ferelin.core.view.DialogErrorFragment
import java.util.*
import kotlin.concurrent.timerTask

const val SHARING_STOP_TIMEOUT = 5000L

fun showDialog(text: String, fragmentManager: FragmentManager) {
    DialogErrorFragment
        .newInstance(text)
        .show(fragmentManager, null)
}

// TODO remove
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