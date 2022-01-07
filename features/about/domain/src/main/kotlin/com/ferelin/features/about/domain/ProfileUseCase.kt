package com.ferelin.features.about.domain

import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entities.entity.CompanyId
import com.ferelin.core.domain.entities.entity.LceState
import com.ferelin.core.domain.entities.entity.Profile
import com.ferelin.features.about.domain.repositories.ProfileRepository
import kotlinx.coroutines.flow.*
import javax.inject.Inject

interface ProfileUseCase {
  val profileLce: Flow<LceState>
  fun getProfileBy(companyId: CompanyId): Flow<Profile>
}

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