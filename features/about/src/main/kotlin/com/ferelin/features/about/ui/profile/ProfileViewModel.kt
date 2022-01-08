package com.ferelin.features.about.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.ProfileUseCase
import com.ferelin.core.ui.params.ProfileParams
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.zip

internal class ProfileViewModel(
  profileParams: ProfileParams,
  profileUseCase: ProfileUseCase,
  companyUseCase: CompanyUseCase
) : ViewModel() {
  val profileLce = profileUseCase.profileLce
  val profile = companyUseCase.companies
    .map { companies -> companies.find { it.id == profileParams.companyId } }
    .filterNotNull()
    .zip(
      other = profileUseCase.getProfileBy(profileParams.companyId),
      transform = { company, profile -> ProfileMapper.map(profile, company) }
    )

  fun onPhoneClick(phone: String) {
    // open contacts
  }

  fun onUrlClick() {
    // open url
  }
}

internal class ProfileViewModelFactory @AssistedInject constructor(
  @Assisted(PROFILE_PARAMS) private val profileParams: ProfileParams,
  private val profileUseCase: ProfileUseCase,
  private val companyUseCase: CompanyUseCase
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    require(modelClass == ProfileViewModel::class)
    return ProfileViewModel(profileParams, profileUseCase, companyUseCase) as T
  }

  @AssistedFactory
  interface Factory {
    fun create(@Assisted(PROFILE_PARAMS) profileParams: ProfileParams): ProfileViewModelFactory
  }
}

internal const val PROFILE_PARAMS = "profile-params"