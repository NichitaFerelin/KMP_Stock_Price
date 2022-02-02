package com.ferelin.core.domain.usecase

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.entity.Profile
import com.ferelin.core.domain.repository.ProfileRepository
import dagger.Reusable
import kotlinx.coroutines.flow.*
import javax.inject.Inject

interface ProfileUseCase {
  val profileLce: Flow<LceState>
  fun getProfileBy(companyId: CompanyId): Flow<Profile>
}

@Reusable
internal class ProfileUseCaseImpl @Inject constructor(
  private val profileRepository: ProfileRepository,
  private val dispatchersProvider: DispatchersProvider
) : ProfileUseCase {
  override fun getProfileBy(companyId: CompanyId): Flow<Profile> {
    return profileRepository.getBy(companyId)
      .onStart { profileLceState.value = LceState.Loading }
      .onEach { profileLceState.value = LceState.Content }
      .catch { e -> profileLceState.value = LceState.Error(e.message) }
      .flowOn(dispatchersProvider.IO)
  }

  private val profileLceState = MutableStateFlow<LceState>(LceState.None)
  override val profileLce: Flow<LceState> = profileLceState.asStateFlow()
}