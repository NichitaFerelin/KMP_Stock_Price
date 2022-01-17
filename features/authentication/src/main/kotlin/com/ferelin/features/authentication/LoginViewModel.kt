package com.ferelin.features.authentication

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.repository.AuthState
import com.ferelin.core.domain.usecase.AuthUseCase
import com.ferelin.core.network.NetworkListener
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

internal data class LoginStateUi(
  val inputPhone: String = "",
  val inputCode: String = "",
  val authState: AuthState = AuthState.None,
  val showNetworkError: Boolean = false
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
      .filterNot { viewModelState.value.authState.isProcessing() }
      .onEach(authUseCase::completeAuthentication)
      .flowOn(dispatchersProvider.IO)
      .launchIn(viewModelScope)

    authUseCase.authState
      .onEach(this::onAuthState)
      .launchIn(viewModelScope)
  }

  fun onSendCodeClick(authHolder: Activity) {
    viewModelScope.launch(dispatchersProvider.IO) {
      val phoneNumber = viewModelState.value.inputPhone
      authUseCase.tryAuthentication(authHolder, "+$phoneNumber")
    }
  }

  fun onPhoneChanged(phone: String) {
    viewModelState.update { it.copy(inputPhone = phone) }
  }

  fun onCodeChanged(code: String) {
    if (code.length in 0..AuthUseCase.CODE_REQUIRED_SIZE) {
      _inputCode.value = code
      viewModelState.update { it.copy(inputCode = code) }
    }
  }

  private fun onAuthState(authState: AuthState) {
    viewModelState.update { it.copy(authState = authState) }
  }

  private fun onNetwork(isAvailable: Boolean) {
    viewModelState.update { it.copy(showNetworkError = !isAvailable) }
  }
}

internal fun AuthState.isProcessing(): Boolean {
  return when (this) {
    AuthState.None, AuthState.Error -> true
    else -> false
  }
}

internal class LoginViewModelFactory @Inject constructor(
  private val authUseCase: AuthUseCase,
  private val networkListener: NetworkListener,
  private val dispatchersProvider: DispatchersProvider
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    require(modelClass == LoginViewModel::class.java)
    return LoginViewModel(authUseCase, dispatchersProvider, networkListener) as T
  }
}