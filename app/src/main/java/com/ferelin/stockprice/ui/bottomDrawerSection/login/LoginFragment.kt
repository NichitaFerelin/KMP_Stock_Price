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

package com.ferelin.stockprice.ui.bottomDrawerSection.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentLoginBinding
import com.ferelin.stockprice.ui.bottomDrawerSection.BottomDrawerFragment
import com.ferelin.stockprice.utils.showToast
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment : BaseFragment<FragmentLoginBinding, LoginViewModel, LoginViewController>() {

    override val mViewController = LoginViewController()
    override val mViewModel: LoginViewModel by viewModels()

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoginBinding
        get() = FragmentLoginBinding::inflate

    private val mBottomNavDrawer: BottomDrawerFragment by lazy(LazyThreadSafetyMode.NONE) {
        requireParentFragment() as BottomDrawerFragment
    }

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)
        mBottomNavDrawer.openDrawer()
        setUpListeners()
    }

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch { collectSignInState() }
            launch { collectCodeSentState() }
            launch { collectLoadingState() }
            launch { collectErrorState() }
        }
    }

    private fun setUpListeners() {
        with(mViewController.viewBinding) {
            editTextPhone.addTextChangedListener {
                val phone = it.toString()
                mViewModel.onPhoneNumberChanged(phone)
                mViewController.onPhoneNumberChanged(phone)
            }
            imageViewIconCheck.apply {
                setOnClickListener {
                    mViewModel.onSendCodeClicked(
                        requireActivity(),
                        mViewController.viewBinding.editTextPhone.text.toString()
                    )
                    mViewController.onSendCodeClicked()
                }
                isClickable = false
            }
            editTextCode.addTextChangedListener {
                mViewController.onCodeChanged()
                mViewModel.onCodeChanged(it.toString())
            }
            imageViewBack.setOnClickListener {
                parentFragmentManager.popBackStack()
            }
        }
    }

    private suspend fun collectSignInState() {
        mViewModel.stateSignIn.collect {
            withContext(mCoroutineContext.Main) {
                showToast(requireContext(), getString(R.string.hintSuccess))
                parentFragmentManager.popBackStack()
            }
        }
    }

    private suspend fun collectCodeSentState() {
        mViewModel.stateCodeSent.collect { isCodeSent ->
            withContext(mCoroutineContext.Main) {
                mViewController.onCodeSentStateChanged(isCodeSent)
            }
        }
    }

    private suspend fun collectLoadingState() {
        mViewModel.stateIsLoading.collect { isLoading ->
            withContext(mCoroutineContext.Main) {
                mViewController.onLoadingStateChanged(isLoading)
            }
        }
    }

    private suspend fun collectErrorState() {
        mViewModel.eventError.collect { error ->
            withContext(mCoroutineContext.Main) {
                mViewController.onError(error)
            }
        }
    }
}