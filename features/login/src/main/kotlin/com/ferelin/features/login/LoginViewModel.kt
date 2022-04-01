package com.ferelin.features.login

import android.app.Activity
import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.repository.AuthState
import com.ferelin.core.domain.usecase.AuthUseCase
import com.ferelin.core.network.NetworkListener
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

@Immutable
internal data class LoginStateUi(
  val inputPhone: String = "",
  val inputPhoneEnabled: Boolean = true,
  val inputCode: String = "",
  val inputCodeVisible: Boolean = false,
  val inputCodeEnabled: Boolean = false,
  val sendCodeEnabled: Boolean = false,
  val networkError: Boolean = false,
  val emptyPhoneError: Boolean = false,
  val tooManyRequestsError: Boolean = false,
  val undefinedError: Boolean = false,
  val loading: Boolean = false,
  val verificationComplete: Boolean = false
)

internal class LoginViewModel(
  private val authUseCase: AuthUseCase,
  private val dispatchersProvider: DispatchersProvider,
  networkListener: NetworkListener,
) : ViewModel() {
  private val viewModelState = MutableStateFlow(LoginStateUi())
  val uiState = viewModelState.asStateFlow()

  private val _inputCode = MutableStateFlow("")

  init {
    networkListener.networkState
      .distinctUntilChanged()
      .onEach(this::onNetwork)
      .filter { networkAvailable -> networkAvailable }
      .combine(
        flow = _inputCode,
        transform = { _, inputCode -> inputCode }
      )
      .filter { it.length == AuthUseCase.CODE_REQUIRED_SIZE }
      .onEach(authUseCase::completeAuthentication)
      .flowOn(dispatchersProvider.IO)
      .launchIn(viewModelScope)

    authUseCase.authState
      .onEach(this::onAuthState)
      .launchIn(viewModelScope)
  }

  fun onSendCodeClick(authHolder: Activity) {
    viewModelState.update { it.copy(inputCodeEnabled = false) }

    viewModelScope.launch(dispatchersProvider.IO) {
      val phoneNumber = viewModelState.value.inputPhone
      authUseCase.tryAuthentication(authHolder, "+$phoneNumber")
    }
  }

  fun onPhoneChanged(phone: String) {
    viewModelState.update {
      it.copy(
        inputPhone = phone,
        sendCodeEnabled = phone.isNotEmpty(),
        inputCode = "",
        inputCodeEnabled = false,
        inputCodeVisible = false
      )
    }
  }

  fun onCodeChanged(code: String) {
    if (code.length in 0..AuthUseCase.CODE_REQUIRED_SIZE) {
      _inputCode.value = code
      viewModelState.update { it.copy(inputCode = code) }
    }
  }

  private fun onAuthState(authState: AuthState) {
    when (authState) {
      AuthState.PhoneProcessing -> onPhoneProcessing()
      AuthState.CodeSent -> onCodeSent()
      AuthState.CodeProcessing -> onCodeProcessing()
      AuthState.EmptyPhone -> onEmptyPhone()
      AuthState.TooManyRequests -> onTooManyRequests()
      AuthState.Error -> onUndefinedError()
      AuthState.VerificationComplete -> onVerificationComplete()
      else -> Unit
    }
  }

  private fun onPhoneProcessing() {
    viewModelState.update {
      it.copy(inputPhoneEnabled = false, loading = true)
    }
  }

  private fun onCodeSent() {
    viewModelState.update {
      it.copy(
        inputCodeEnabled = true,
        inputCodeVisible = true,
        loading = false
      )
    }
  }

  private fun onCodeProcessing() {
    viewModelState.update {
      it.copy(
        inputPhoneEnabled = false,
        inputCodeEnabled = false,
        loading = true
      )
    }
  }

  private fun onEmptyPhone() {
    viewModelState.update {
      it.copy(
        loading = false,
        emptyPhoneError = true
      )
    }
  }

  private fun onTooManyRequests() {
    viewModelState.update {
      it.copy(
        loading = false,
        tooManyRequestsError = true
      )
    }
  }

  private fun onUndefinedError() {
    viewModelState.update {
      it.copy(
        undefinedError = true,
        loading = false
      )
    }
  }

  private fun onVerificationComplete() {
    viewModelState.update {
      it.copy(
        verificationComplete = true,
        loading = false
      )
    }
  }

  private fun onNetwork(isAvailable: Boolean) {
    viewModelState.update { it.copy(networkError = !isAvailable) }
  }
}