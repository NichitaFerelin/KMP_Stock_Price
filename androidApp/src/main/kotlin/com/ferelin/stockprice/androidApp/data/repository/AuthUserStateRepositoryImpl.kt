package com.ferelin.stockprice.androidApp.data.repository

import com.ferelin.stockprice.androidApp.domain.repository.AuthUserStateRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow

internal class AuthUserStateRepositoryImpl(
  private val firebaseAuth: FirebaseAuth
) : AuthUserStateRepository {
  override val userAuthenticated: Flow<Boolean> = callbackFlow {
    trySend(firebaseAuth.currentUser != null)

    val authListener = FirebaseAuth.AuthStateListener {
      userTokenState.value = firebaseAuth.uid ?: ""
      trySend(firebaseAuth.currentUser != null)
    }
    firebaseAuth.addAuthStateListener(authListener)
    awaitClose { firebaseAuth.removeAuthStateListener(authListener) }
  }

  private val userTokenState = MutableStateFlow(firebaseAuth.uid ?: "")
  override val userToken: Flow<String> = userTokenState.asStateFlow()
}
