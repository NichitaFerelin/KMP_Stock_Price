package com.ferelin.common.domain.usecase

import com.ferelin.stockprice.domain.entity.CompanyId
import com.ferelin.stockprice.domain.entity.LceState
import com.ferelin.stockprice.domain.entity.Profile
import com.ferelin.common.domain.repository.ProfileRepository
import kotlinx.coroutines.flow.*

interface ProfileUseCase {
  val profileLce: Flow<LceState>
  fun getProfileBy(companyId: CompanyId): Flow<Profile>
}

internal class ProfileUseCaseImpl(
  private val profileRepository: ProfileRepository
) : ProfileUseCase {
  override fun getProfileBy(companyId: CompanyId): Flow<Profile> {
    return profileRepository.getBy(companyId)
      .onStart { profileLceState.value = LceState.Loading }
      .onEach { profileLceState.value = LceState.Content }
      .catch { e -> profileLceState.value = LceState.Error(e.message) }
  }

  private val profileLceState = MutableStateFlow<LceState>(LceState.None)
  override val profileLce: Flow<LceState> = profileLceState.asStateFlow()
}