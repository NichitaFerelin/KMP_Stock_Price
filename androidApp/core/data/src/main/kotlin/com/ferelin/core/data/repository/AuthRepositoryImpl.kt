package com.ferelin.core.data.repository

import android.app.Activity
import com.ferelin.core.domain.repository.AuthRepository
import com.ferelin.core.domain.repository.AuthState
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.concurrent.TimeUnit

internal class AuthRepositoryImpl(
  private val firebaseAuth: FirebaseAuth,
) : AuthRepository {
  private val authProcessingState = MutableStateFlow(AuthState.None)
  override val authProcessing: Flow<AuthState> = authProcessingState.asStateFlow()

  // User ID is used to complete verification
  @Volatile
  private var userVerificationId: String? = null

  @Volatile
  private var authCallbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks? = null

  override suspend fun tryAuthentication(holder: Activity, phone: String) {
    authProcessingState.value = AuthState.PhoneProcessing

    if (phone.isEmpty()) {
      authProcessingState.value = AuthState.EmptyPhone
      return
    }

    val verificationCallbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
      override fun onCodeSent(verificationId: String, p1: PhoneAuthProvider.ForceResendingToken) {
        userVerificationId = verificationId
        authProcessingState.value = AuthState.CodeSent
      }

      override fun onVerificationCompleted(credential: PhoneAuthCredential) {
        authProcessingState.value = AuthState.CodeProcessing
        firebaseAuth.signInWithCredential(credential)
          .addOnCompleteListener {
            authProcessingState.value = AuthState.VerificationComplete
          }
          .addOnFailureListener {
            authProcessingState.value = AuthState.VerificationCompletionError
          }
      }

      override fun onVerificationFailed(exc: FirebaseException) {
        authProcessingState.value = when (exc) {
          is FirebaseTooManyRequestsException -> AuthState.TooManyRequests
          else -> AuthState.Error
        }
      }
    }

    val options = PhoneAuthOptions.newBuilder(firebaseAuth)
      .setPhoneNumber(phone)
      .setTimeout(AUTH_TIMEOUT, TimeUnit.SECONDS)
      .setActivity(holder)
      .setCallbacks(verificationCallbacks)
      .build()
    PhoneAuthProvider.verifyPhoneNumber(options)

    authCallbacks = verificationCallbacks
  }

  override suspend fun completeAuthentication(code: String) {
    userVerificationId?.let { userVerificationId ->
      val credential = PhoneAuthProvider.getCredential(userVerificationId, code)
      authCallbacks?.onVerificationCompleted(credential)
    } ?: error("Attempt to complete authentication with null user ID")
  }

  override suspend fun logOut() {
    authProcessingState.value = AuthState.None
    firebaseAuth.signOut()
  }
}

internal const val AUTH_TIMEOUT = 30L