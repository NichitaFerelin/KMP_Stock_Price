package com.ferelin.features.authentication.ui

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.network.NetworkListener
import com.ferelin.features.authentication.domain.AuthUseCase
import com.ferelin.features.authentication.domain.repository.AuthState
import com.ferelin.navigation.Router
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class LoginViewModel @Inject constructor(
  private val authUseCase: AuthUseCase,
  private val router: Router,
  networkListener: NetworkListener,
) : ViewModel() {
  private val _inputCode = MutableStateFlow("")

  val networkState = networkListener.networkState
    .distinctUntilChanged()
  val authState = authUseCase.authState
  val authCodeRequiredSize = AuthUseCase.CODE_REQUIRED_SIZE

  init {
    authUseCase.userAuth
      .distinctUntilChanged()
      .filter { it }
      .onEach(this::onUserAuthenticated)
      .launchIn(viewModelScope)

    networkState
      .filter { networkAvailable -> networkAvailable }
      .combine(
        flow = _inputCode,
        transform = { _, inputCode -> inputCode }
      )
      .filter { it.length == authCodeRequiredSize }
      .filterNot { authProcessing() }
      .onEach(authUseCase::completeAuthentication)
      .launchIn(viewModelScope)
  }

  fun tryAuthenticate(authHolder: Activity, phoneNumber: String) {
    viewModelScope.launch {
      authUseCase.tryAuthentication(authHolder, phoneNumber)
    }
  }

  fun onCodeChanged(code: String) {
    _inputCode.value = code
  }

  fun onBack() {
    // back
  }

  private fun onUserAuthenticated(isAuthenticated: Boolean) {
    // notify and back
  }

  private suspend fun authProcessing(): Boolean {
    return when (authState.firstOrNull()) {
      AuthState.None, AuthState.Error -> true
      else -> false
    }
  }
}