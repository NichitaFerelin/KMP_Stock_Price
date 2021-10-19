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
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.core.utils.animManager.AnimationManager
import com.ferelin.core.utils.animManager.invalidate
import com.ferelin.core.utils.isOut
import com.ferelin.core.utils.setOnClick
import com.ferelin.core.view.BaseFragment
import com.ferelin.core.viewModel.BaseViewModelFactory
import com.ferelin.domain.sources.AuthResponse
import com.ferelin.feature_login.R
import com.ferelin.feature_login.databinding.FragmentLoginBinding
import com.ferelin.feature_login.viewModel.LoginViewModel
import com.ferelin.shared.LoadState
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoginBinding
        get() = FragmentLoginBinding::inflate

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<LoginViewModel>

    private val viewModel: LoginViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    private var scaleIn: Animation? = null
    private var scaleOut: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
            .apply { duration = 200L }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
            .apply { duration = 200L }
    }

    override fun initUi() {
        viewBinding.editTextCode.filters += InputFilter.LengthFilter(viewModel.requiredCodeSize)
    }

    override fun initUx() {
        with(viewBinding) {
            editTextCode.addTextChangedListener { charSequence ->
                viewModel.onCodeChanged(charSequence.toString())
            }
            editTextPhone.addTextChangedListener { charSequence ->
                onPhoneChanged(charSequence.toString())
            }

            imageViewBack.setOnClick(viewModel::onBackClick)
            imageViewIconCheck.setOnClickListener {
                viewModel.onSendCodeClick(
                    holderActivity = requireActivity(),
                    phone = viewBinding.editTextPhone.text?.toString() ?: ""
                )
            }
        }
    }

    override fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch {
            launch { observeAuthenticationState() }
            launch { observeNetworkState() }
        }
    }

    override fun onDestroyView() {
        scaleIn?.invalidate()
        scaleOut?.invalidate()
        super.onDestroyView()
    }

    private suspend fun observeAuthenticationState() {
        viewModel.authenticationLoad.collect { authLoadState ->
            withContext(Dispatchers.Main) {
                when (authLoadState) {
                    is LoadState.Loading -> onLoading(authLoadState)
                    is LoadState.Prepared -> onPrepared(authLoadState)
                    is LoadState.Error -> onError(authLoadState)
                    is LoadState.None -> Unit
                }
            }
        }
    }

    private suspend fun observeNetworkState() {
        viewModel.networkState.collect { isAvailable ->
            withContext(Dispatchers.Main) {
                if (isAvailable) {
                    showSnackbar(getString(R.string.messageNetworkAvailable))
                } else {
                    showTempSnackbar(getString(R.string.messageNetworkNotAvailable))
                }
            }
        }
    }

    private fun onLoading(loadingState: LoadState.Loading<AuthResponse>) {
        when (loadingState.data) {
            AuthResponse.CodeProcessing -> {
                hideKeyboard()
                showProgressBar()
            }
            AuthResponse.PhoneProcessing -> {
                hideKeyboard()
                hideCheckIcon()
                showProgressBar()
            }
            else -> Unit
        }
    }

    private fun onPrepared(preparedState: LoadState.Prepared<AuthResponse>) {
        when (preparedState.data) {
            AuthResponse.CodeSent -> {
                hideProgressBar()
                hideCheckIcon()
                showEnterCodeField()
                showKeyboard(viewBinding.editTextCode)
            }
            AuthResponse.Complete -> {
                hideProgressBar()
            }
            else -> Unit
        }
    }

    private fun onError(errorState: LoadState.Error<AuthResponse>) {
        hideProgressBar()
        hideCheckIcon()

        when (errorState.data) {
            AuthResponse.TooManyRequests -> {
                hideEnterCodeField()
                viewBinding.editTextPhone.error = getString(R.string.errorTooManyRequests)
            }
            AuthResponse.EmptyPhone -> {
                hideEnterCodeField()
                viewBinding.editTextPhone.error = getString(R.string.errorEmptyPhone)
            }
            else -> {
                showTempSnackbar(getString(R.string.errorUndefined))
            }
        }
    }

    private fun onPhoneChanged(phone: String) {
        if (phone.isEmpty()) {
            hideCheckIcon()
        } else {
            showCheckIcon()
        }

        hideEnterCodeField()
    }

    private fun hideCheckIcon() {
        if (!viewBinding.imageViewIconCheck.isOut) {

            if (scaleOut == null) {
                scaleOut = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_out)
            }

            val callback = object : AnimationManager() {
                override fun onAnimationEnd(animation: Animation?) {
                    viewBinding.imageViewIconCheck.isOut = true
                }
            }

            scaleOut!!.setAnimationListener(callback)
            viewBinding.imageViewIconCheck.startAnimation(scaleOut!!)
        }
    }

    private fun showCheckIcon() {
        if (viewBinding.imageViewIconCheck.isOut) {

            if (scaleIn == null) {
                scaleIn = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_in)
            }

            val callback = object : AnimationManager() {
                override fun onAnimationStart(animation: Animation?) {
                    viewBinding.imageViewIconCheck.isOut = false
                }
            }

            scaleIn!!.setAnimationListener(callback)
            viewBinding.imageViewIconCheck.startAnimation(scaleIn!!)
        }
    }

    private fun showProgressBar() {
        viewBinding.progressBar.isOut = false
    }

    private fun hideProgressBar() {
        viewBinding.progressBar.isOut = true
    }

    private fun showEnterCodeField() {
        viewBinding.editTextCodeLayout.alpha = 1F
    }

    private fun hideEnterCodeField() {
        viewBinding.editTextCodeLayout.alpha = 0F
    }

    companion object {
        fun newInstance(data: Any?): LoginFragment {
            return LoginFragment()
        }
    }
}