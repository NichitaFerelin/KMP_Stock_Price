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

package com.ferelin.core.view

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment

class DialogErrorFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return arguments?.let {
            AlertDialog.Builder(requireActivity())
                .setMessage(it[sMessageKeyStr].toString())
                .setPositiveButton("OK") { _, _ -> this@DialogErrorFragment.dismiss() }
                .create()
        } ?: throw IllegalStateException("No arguments was passed for DialogErrorFragment.")
    }

    companion object {
        private const val sMessageKeyStr = "message"

        fun newInstance(message: String): DialogErrorFragment {
            return DialogErrorFragment().apply {
                arguments = bundleOf(sMessageKeyStr to message)
            }
        }
    }
}