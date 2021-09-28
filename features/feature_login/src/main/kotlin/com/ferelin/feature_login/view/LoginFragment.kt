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

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.Slide
import com.ferelin.core.base.BaseFragment
import com.ferelin.core.base.BaseViewModelFactory
import com.ferelin.domain.sources.AuthenticationState
import com.ferelin.feature_login.R
import com.ferelin.feature_login.databinding.FragmentLoginBinding
import com.ferelin.feature_login.viewModel.AuthenticationLoadState
import com.ferelin.feature_login.viewModel.LoginViewModel
import com.google.android.material.transition.MaterialContainerTransform
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
        enterTransition = MaterialContainerTransform().apply {
            // TODO startView = requireActivity().findViewById(R.id.mainFab)
            endViewId = R.id.loginRoot
            scrimColor = Color.TRANSPARENT
            duration = 350L
        }
    }

    override fun initUx() {
        // set listeners for phone input
        // mViewModel.tryToLogIn(requireActivity(), "")
    }

    override fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launchWhenResumed {
            observeAuthenticationState()
        }
    }

    private suspend fun observeAuthenticationState() {
        mViewModel.authenticationState.collect { loadState ->
            when (loadState) {
                is AuthenticationLoadState.Authenticated -> {
                    // replace fragment
                }
                is AuthenticationLoadState.Loading -> {
                    if (loadState.state == AuthenticationState.CodeSent) {
                        // show field for text input
                    } else {
                        // show progress bar
                    }
                }
                is AuthenticationLoadState.Error -> {
                    // show
                }
                is AuthenticationLoadState.None -> {
                    // update ui
                }
            }
        }
    }
}