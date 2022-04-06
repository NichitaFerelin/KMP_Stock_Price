package com.ferelin.stockprice.androidApp.domain.usecase

import android.app.Activity
import com.ferelin.stockprice.androidApp.domain.repository.AuthRepository
import com.ferelin.stockprice.androidApp.domain.repository.AuthState
import com.ferelin.stockprice.androidApp.domain.repository.AuthUserStateRepository
import com.ferelin.stockprice.shared.domain.repository.FavouriteCompanyRepository
import com.ferelin.stockprice.shared.domain.repository.SearchRequestsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

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

internal class AuthUseCaseImpl(
  private val authRepository: AuthRepository,
  private val searchRequestsRepository: SearchRequestsRepository,
  private val favouriteCompanyRepository: FavouriteCompanyRepository,
  private val externalScope: CoroutineScope,
  authUserStateRepository: AuthUserStateRepository
) : AuthUseCase {
  override val userAuth: Flow<Boolean> =
    authUserStateRepository.userAuthenticated.distinctUntilChanged()

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

