package com.ferelin.core.domain.usecase

import android.app.Activity
import com.ferelin.core.ExternalScope
import com.ferelin.core.domain.repository.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

interface AuthUseCase {
  val userAuth: Flow<Boolean>
  val authState: Flow<AuthState>
  suspend fun tryAuthentication(holder: Activity, phone: String)
  suspend fun completeAuthentication(code: String)
  suspend fun logOut()

  companion object {
    const val CODE_REQUIRED_SIZE = 6
  }
}

internal class AuthUseCaseImpl @Inject constructor(
  private val authRepository: AuthRepository,
  private val searchRequestsRepository: SearchRequestsRepository,
  private val favouriteCompanyRepository: FavouriteCompanyRepository,
  @ExternalScope private val externalScope: CoroutineScope,
  authUserStateRepository: AuthUserStateRepository
) : AuthUseCase {
  override val userAuth: Flow<Boolean> = authUserStateRepository.userAuthenticated.distinctUntilChanged()
  override val authState: Flow<AuthState> = authRepository.authProcessing.distinctUntilChanged()

  override suspend fun tryAuthentication(holder: Activity, phone: String) {
    authRepository.tryAuthentication(holder, phone)
  }

  override suspend fun completeAuthentication(code: String) {
    authRepository.completeAuthentication(code)
  }

  override suspend fun logOut() {
    authRepository.logOut()
    externalScope.launch {
      searchRequestsRepository.eraseAll()
      favouriteCompanyRepository.eraseAll(false)
    }
  }
}

