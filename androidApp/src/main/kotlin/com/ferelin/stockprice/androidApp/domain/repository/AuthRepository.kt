package com.ferelin.stockprice.androidApp.domain.repository

import android.app.Activity
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
  val authProcessing: Flow<AuthState>
  suspend fun tryAuthentication(holder: Activity, phone: String)
  suspend fun completeAuthentication(code: String)
  suspend fun logOut()
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