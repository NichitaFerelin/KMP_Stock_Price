package com.ferelin.stockprice.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment

class DialogErrorFragment : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return arguments?.let {
            AlertDialog.Builder(requireActivity())
                .setMessage(it[sMessageStr].toString())
                .setPositiveButton("OK") { _, _ -> this@DialogErrorFragment.dismiss() }
                .create()
        } ?: throw IllegalStateException("No arguments was passed for DialogErrorFragment.")
    }

    companion object {
        private const val sMessageStr = "messagestr"

        fun newInstance(message: String): DialogErrorFragment {
            return DialogErrorFragment().apply {
                arguments = bundleOf(sMessageStr to message)
            }
        }
    }
}