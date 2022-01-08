package com.ferelin.features.authentication.ui

import android.app.Activity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.domain.repository.AuthState
import com.ferelin.core.domain.usecase.AuthUseCase
import com.ferelin.core.network.NetworkListener
import com.ferelin.core.ui.view.routing.Coordinator
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class LoginViewModel @Inject constructor(
  private val authUseCase: AuthUseCase,
  private val coordinator: Coordinator,
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
      .map { }
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
    coordinator.onEvent(LoginRouteEvent.BackRequested)
  }

  private fun onUserAuthenticated(unit: Unit) {
    coordinator.onEvent(LoginRouteEvent.UserAuthenticated)
  }

  private suspend fun authProcessing(): Boolean {
    return when (authState.firstOrNull()) {
      AuthState.None, AuthState.Error -> true
      else -> false
    }
  }
}