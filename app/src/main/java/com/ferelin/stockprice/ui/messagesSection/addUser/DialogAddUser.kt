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

package com.ferelin.stockprice.ui.messagesSection.addUser

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.setFragmentResult
import com.ferelin.stockprice.R
import com.ferelin.stockprice.databinding.DialogAddUserBinding

class DialogAddUser : DialogFragment() {

    private var mBinding: DialogAddUserBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        mBinding = DialogAddUserBinding.inflate(inflater, container, false)
        return mBinding!!.root
    }

    override fun onResume() {
        super.onResume()
        dialog?.window?.setLayout(
            resources.getDimension(R.dimen.dialogWidth).toInt(),
            resources.getDimension(R.dimen.dialogHeight).toInt()
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListeners()
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

    private fun setUpListeners() {
        mBinding!!.run {
            imageViewDone.setOnClickListener {
                setFragmentResult(
                    ADD_USER_REQUEST_KEY, bundleOf(
                        USER_LOGIN_KEY to mBinding!!.editTextUserLogin.text.toString()
                    )
                )
                dismiss()
            }
        }
    }

    companion object {
        const val ADD_USER_REQUEST_KEY = "add_user_request_key"
        const val USER_LOGIN_KEY = "user_login_key"
    }
}