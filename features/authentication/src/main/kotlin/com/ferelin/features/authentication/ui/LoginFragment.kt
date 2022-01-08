package com.ferelin.features.authentication.ui

import android.os.Bundle
import android.text.InputFilter
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.ferelin.core.domain.repository.AuthState
import com.ferelin.core.ui.view.BaseFragment
import com.ferelin.core.ui.view.launchAndRepeatWithViewLifecycle
import com.ferelin.core.ui.view.setOnClick
import com.ferelin.core.ui.viewModel.BaseViewModelFactory
import com.ferelin.features.authentication.databinding.FragmentLoginBinding
import com.google.android.material.transition.MaterialSharedAxis
import dagger.Lazy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

internal class LoginFragment : BaseFragment<FragmentLoginBinding>() {
  override val bindingInflater: (LayoutInflater, ViewGroup?, Boolean) -> FragmentLoginBinding
    get() = FragmentLoginBinding::inflate

  @Inject
  lateinit var viewModelFactory: Lazy<BaseViewModelFactory<LoginViewModel>>
  private val viewModel: LoginViewModel by viewModels(
    factoryProducer = { viewModelFactory.get() }
  )

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enterTransition = MaterialSharedAxis(MaterialSharedAxis.Z, true)
      .apply { duration = 200L }
    returnTransition = MaterialSharedAxis(MaterialSharedAxis.Z, false)
      .apply { duration = 200L }
  }

  override fun initUi() {
    viewBinding.editTextCode.filters += InputFilter.LengthFilter(viewModel.authCodeRequiredSize)
  }

  override fun initUx() {
    with(viewBinding) {
      editTextCode.addTextChangedListener { charSequence ->
        viewModel.onCodeChanged(charSequence.toString())
      }
      editTextPhone.addTextChangedListener { charSequence ->
        onPhoneChanged(charSequence.toString())
      }

      imageViewBack.setOnClick(viewModel::onBack)
      imageViewIconCheck.setOnClickListener {
        viewModel.tryAuthenticate(
          authHolder = requireActivity(),
          phoneNumber = viewBinding.editTextPhone.text?.toString() ?: ""
        )
      }
    }
  }

  override fun initObservers() {
    with(viewModel) {
      launchAndRepeatWithViewLifecycle {
        authState
          .flowOn(Dispatchers.Main)
          .onEach(this@LoginFragment::onAuthState)
          .launchIn(viewLifecycleOwner.lifecycleScope)

        networkState
          .flowOn(Dispatchers.Main)
          .onEach(this@LoginFragment::onNetwork)
          .launchIn(viewLifecycleOwner.lifecycleScope)
      }
    }
  }

  private fun onAuthState(authState: AuthState) {
    when (authState) {
      AuthState.None -> Unit
      AuthState.EmptyPhone -> Unit
      AuthState.PhoneProcessing -> Unit
      AuthState.CodeSent -> Unit
      AuthState.CodeProcessing -> Unit
      AuthState.TooManyRequests -> Unit
      AuthState.VerificationCompletionError -> Unit
      AuthState.VerificationComplete -> Unit
      AuthState.Error -> Unit
    }
  }

  private fun onNetwork(isNetworkAvailable: Boolean) {
    if (isNetworkAvailable) {
      // notify
    } else {
      // notify
    }
  }

  private fun onPhoneChanged(phone: String) {
    if (phone.isEmpty()) hideCheckIcon() else showCheckIcon()
    hideEnterCodeField()
  }

  private fun hideCheckIcon() {
    viewBinding.imageViewIconCheck.isVisible = false
  }

  private fun showCheckIcon() {
    viewBinding.imageViewIconCheck.isVisible = true
  }

  private fun showEnterCodeField() {
    viewBinding.editTextCodeLayout.alpha = 1F
  }

  private fun hideEnterCodeField() {
    viewBinding.editTextCodeLayout.alpha = 0F
  }
}