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

package com.ferelin.stockprice.ui.login

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.Slide
import com.ferelin.stockprice.R
import com.ferelin.stockprice.base.BaseFragment
import com.ferelin.stockprice.databinding.FragmentLoginBinding
import com.google.android.material.transition.MaterialContainerTransform
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginFragment(
    private val mIsReplacedFromMenu: Boolean? = null
) : BaseFragment<FragmentLoginBinding, LoginViewModel, LoginViewController>() {

    override val mViewController = LoginViewController()
    override val mViewModel: LoginViewModel by viewModels()

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoginBinding
        get() = FragmentLoginBinding::inflate

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        returnTransition = Slide().apply {
            duration = 225L
            addTarget(R.id.loginRoot)
        }
        enterTransition = if (mIsReplacedFromMenu == true) {
            MaterialFadeThrough().apply {
                duration = 200L
            }
        } else {
            MaterialContainerTransform().apply {
                startView = requireActivity().findViewById(R.id.mainFab)
                endViewId = R.id.loginRoot
                scrimColor = Color.TRANSPARENT
                duration = 350L
            }
        }
    }

    override fun setUpViewComponents(savedInstanceState: Bundle?) {
        super.setUpViewComponents(savedInstanceState)
        setUpListeners()
    }

    override fun initObservers() {
        super.initObservers()
        viewLifecycleOwner.lifecycleScope.launch(mCoroutineContext.IO) {
            launch { collectAuthenticationState() }
            launch { collectEventError() }
        }
    }

    override fun onBackPressedHandle(): Boolean {
        return mViewController.onBackPressed()
    }

    private suspend fun collectAuthenticationState() {
        mViewModel.stateAuthenticationProcess.collect { notificator ->
            withContext(mCoroutineContext.Main) {
                mViewController.onAuthenticationStateChanged(notificator)
            }
        }
    }

    private suspend fun collectEventError() {
        mViewModel.eventOnError.collect { message ->
            withContext(mCoroutineContext.Main) {
                mViewController.onError(message)
            }
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
                mViewController.onBackPressed()
            }
        }
    }
}