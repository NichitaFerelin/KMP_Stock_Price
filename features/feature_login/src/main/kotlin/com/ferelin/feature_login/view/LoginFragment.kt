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
import com.ferelin.core.utils.setOnClick
import com.ferelin.core.view.BaseFragment
import com.ferelin.core.viewModel.BaseViewModelFactory
import com.ferelin.domain.sources.AuthResponse
import com.ferelin.feature_login.R
import com.ferelin.feature_login.databinding.FragmentLoginBinding
import com.ferelin.feature_login.viewModel.LoginViewModel
import com.ferelin.shared.LoadState
import com.google.android.material.transition.MaterialSharedAxis
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

class LoginFragment : BaseFragment<FragmentLoginBinding>() {

    override val mBindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoginBinding
        get() = FragmentLoginBinding::inflate

    @Inject
    lateinit var viewModelFactory: BaseViewModelFactory<LoginViewModel>

    private val mViewModel: LoginViewModel by viewModels(
        factoryProducer = { viewModelFactory }
    )

    private var mScaleIn: Animation? = null
    private var mScaleOut: Animation? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
            .apply { duration = 200L }
        returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
            .apply { duration = 200L }
    }

    override fun initUi() {
        mViewBinding.editTextCode.filters += InputFilter.LengthFilter(mViewModel.requiredCodeSize)
    }

    override fun initUx() {
        with(mViewBinding) {
            editTextCode.addTextChangedListener { charSequence ->
                mViewModel.onCodeChanged(charSequence.toString())
            }
            editTextPhone.addTextChangedListener { charSequence ->
                onPhoneChanged(charSequence.toString())
            }

            imageViewBack.setOnClick(mViewModel::onBackClick)
            imageViewIconCheck.setOnClickListener {
                mViewModel.onSendCodeClick(
                    holderActivity = requireActivity(),
                    phone = mViewBinding.editTextPhone.text?.toString() ?: ""
                )
            }
        }
    }

    override fun initObservers() {
        viewLifecycleOwner.lifecycleScope.launch(mDispatchersProvider.IO) {
            launch { observeAuthenticationState() }
            launch { observeNetworkState() }
        }
    }

    private suspend fun observeAuthenticationState() {
        mViewModel.authenticationLoadState.collect { authLoadState ->
            withContext(mDispatchersProvider.Main) {
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
        mViewModel.networkState.collect { isAvailable ->
            withContext(mDispatchersProvider.Main) {
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
                showKeyboard(mViewBinding.editTextCode)
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
                mViewBinding.editTextPhone.error = getString(R.string.errorTooManyRequests)
            }
            AuthResponse.EmptyPhone -> {
                hideEnterCodeField()
                mViewBinding.editTextPhone.error = getString(R.string.errorEmptyPhone)
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
        // TODO const
        if (mViewBinding.imageViewIconCheck.scaleX == 1F) {

            if (mScaleOut == null) {
                mScaleOut = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_out)
            }

            val callback = object : AnimationManager() {
                override fun onAnimationEnd(animation: Animation?) {
                    mViewBinding.imageViewIconCheck.scaleX = 0F
                    mViewBinding.imageViewIconCheck.scaleY = 0F
                }
            }

            mScaleOut!!.setAnimationListener(callback)
            mViewBinding.imageViewIconCheck.startAnimation(mScaleOut!!)
        }
    }

    private fun showCheckIcon() {
        // TODO const
        if (mViewBinding.imageViewIconCheck.scaleX == 0F) {

            if (mScaleIn == null) {
                mScaleIn = AnimationUtils.loadAnimation(requireContext(), R.anim.scale_in)
            }

            val callback = object : AnimationManager() {
                override fun onAnimationStart(animation: Animation?) {
                    mViewBinding.imageViewIconCheck.scaleX = 1F
                    mViewBinding.imageViewIconCheck.scaleY = 1F
                }
            }

            mScaleIn!!.setAnimationListener(callback)
            mViewBinding.imageViewIconCheck.startAnimation(mScaleIn!!)
        }
    }

    private fun showProgressBar() {
        mViewBinding.progressBar.scaleY = 1F
        mViewBinding.progressBar.scaleX = 1F
    }

    private fun hideProgressBar() {
        mViewBinding.progressBar.scaleY = 0F
        mViewBinding.progressBar.scaleX = 0F
    }

    private fun showEnterCodeField() {
        mViewBinding.editTextCodeLayout.alpha = 1F
    }

    private fun hideEnterCodeField() {
        mViewBinding.editTextCodeLayout.alpha = 0F
    }

    companion object {

        fun newInstance(data: Any?): LoginFragment {
            return LoginFragment()
        }
    }
}