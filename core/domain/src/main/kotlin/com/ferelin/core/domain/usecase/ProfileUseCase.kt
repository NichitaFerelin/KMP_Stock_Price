package com.ferelin.core.domain.usecase

import com.ferelin.core.domain.entity.CompanyId
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.entity.Profile
import com.ferelin.core.domain.repository.ProfileRepository
import dagger.Reusable
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

interface ProfileUseCase {
  fun getProfileBy(companyId: CompanyId): Observable<Profile>
  val profileLce: Flow<LceState>
}

@Reusable
internal class ProfileUseCaseImpl @Inject constructor(
  private val profileRepository: ProfileRepository
) : ProfileUseCase {
  override fun getProfileBy(companyId: CompanyId): Observable<Profile> {
    return profileRepository.getBy(companyId)
      .doOnSubscribe { profileLceState.value = LceState.Loading }
      .doOnEach { profileLceState.value = LceState.Content }
      .doOnError { e -> profileLceState.value = LceState.Error(e.message) }
  }

  private val profileLceState = MutableStateFlow<LceState>(LceState.None)
  override val profileLce: Flow<LceState> = profileLceState.asStateFlow()
}