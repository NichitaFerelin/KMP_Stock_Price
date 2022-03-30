package com.ferelin.core.domain.repository

import android.app.Activity
import kotlinx.coroutines.flow.StateFlow

interface AuthRepository {
  val authProcessing: StateFlow<AuthState>
  fun tryAuthentication(holder: Activity, phone: String)
  fun completeAuthentication(code: String)
  fun logOut()
}

enum class AuthState {
  None,
  EmptyPhone,
  PhoneProcessing,
  CodeSent,
  CodeProcessing,
  TooManyRequests,
  VerificationCompletionError,
  VerificationComplete,
  Error
}