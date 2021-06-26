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

package com.ferelin.stockprice.ui.register

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentRegisterBinding
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RegisterFragment :
    BaseFragment<FragmentRegisterBinding, RegisterViewModel, RegisterViewController>() {

    override val mViewController = RegisterViewController()
    override val mViewModel: RegisterViewModel by viewModels()

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentRegisterBinding
        get() = FragmentRegisterBinding::inflate

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)
        setUpListeners()
    }

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch { collectStateRegister() }
            launch { collectEventRegisterError() }
        }
    }

    private fun setUpListeners() {
        mViewController.viewBinding.run {
            imageViewIconCheck.setOnClickListener {
                mViewModel.onIconCheckClicked(mViewController.viewBinding.editTextLogin.text.toString())
            }
            imageViewBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
            editTextLogin.addTextChangedListener {
                mViewController.onLoginChanged()
            }
        }
    }

    private suspend fun collectStateRegister() {
        mViewModel.stateRegistered.collect { registered ->
            if (registered) {
                setFragmentResult(
                    requestKey = REGISTER_REQUEST_KEY,
                    result = bundleOf(REGISTER_RESULT_KEY to true)
                )
                parentFragmentManager.popBackStack()
            }
        }
    }

    private suspend fun collectEventRegisterError() {
        mViewModel.eventRegisterError.collect { message ->
            withContext(mCoroutineContext.Main) {
                mViewController.onError(message)
            }
        }
    }

    companion object {
        const val REGISTER_REQUEST_KEY = "register_request_key"
        const val REGISTER_RESULT_KEY = "register_result_key"
    }
}