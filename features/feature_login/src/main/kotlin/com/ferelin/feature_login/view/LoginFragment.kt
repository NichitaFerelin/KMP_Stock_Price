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

package com.ferelin.feature_login.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.Slide
import com.ferelin.core.base.BaseFragment
import com.ferelin.core.base.BaseViewModelFactory
import com.ferelin.core.utils.setOnClick
import com.ferelin.feature_login.R
import com.ferelin.feature_login.databinding.FragmentLoginBinding
import com.ferelin.feature_login.viewData.AuthProcessingState
import com.ferelin.feature_login.viewModel.LoginViewModel
import com.google.android.material.transition.MaterialFadeThrough
import kotlinx.coroutines.flow.collect
import javax.inject.Inject

class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoginBinding
        get() = FragmentLoginBinding::inflate

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<LoginViewModel>

    private val mViewModel: LoginViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        returnTransition = Slide().apply {
            duration = 225L
            addTarget(R.id.loginRoot)
        }
        enterTransition = MaterialFadeThrough().apply {
            duration = 200L
        }
    }

    override fun initUx() {
        with(mViewBinding) {
            editTextCode.addTextChangedListener { charSequence ->
                charSequence?.let { mViewModel.onCodeChanged(it.toString()) }
            }

            imageViewBack.setOnClick(mViewModel::onBackClicked)
            imageViewIconCheck.setOnClickListener {
                mViewModel.tryToLogIn(
                    holder = requireActivity(),
                    phone = mViewBinding.editTextPhone.text?.toString() ?: ""
                )
            }
        }
    }

    override fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            observeAuthenticationState()
        }
    }

    private suspend fun observeAuthenticationState() {
        mViewModel.authProcessingState.collect { loadState ->
            when (loadState) {
                is AuthProcessingState.None -> {

                }
                is AuthProcessingState.Processing -> {

                }
                is AuthProcessingState.Complete -> {

                }
                is AuthProcessingState.Error -> {

                }
            }
        }
    }
}