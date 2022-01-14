package com.ferelin.features.about.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.ProfileUseCase
import com.ferelin.core.ui.params.ProfileParams
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal data class ProfileStateUi(
  val profile: ProfileViewData = ProfileViewData(),
  val profileLce: LceState = LceState.None
)

internal class ProfileViewModel(
  profileParams: ProfileParams,
  profileUseCase: ProfileUseCase,
  companyUseCase: CompanyUseCase,
  dispatchersProvider: DispatchersProvider
) : ViewModel() {
  private val viewModelState = MutableStateFlow(ProfileStateUi())
  val uiState = viewModelState.asStateFlow()

  init {
    companyUseCase.companies
      .map { companies -> companies.find { it.id == profileParams.companyId } }
      .filterNotNull()
      .zip(
        other = profileUseCase.getProfileBy(profileParams.companyId),
        transform = { company, profile -> ProfileMapper.map(profile, company) }
      )
      .onEach(this::onProfile)
      .flowOn(dispatchersProvider.IO)
      .launchIn(viewModelScope)

    profileUseCase.profileLce
      .onEach(this::onProfileLce)
      .launchIn(viewModelScope)
  }

  private fun onProfile(profileViewData: ProfileViewData) {
    viewModelState.update { it.copy(profile = profileViewData) }
  }

  private fun onProfileLce(lceState: LceState) {
    viewModelState.update { it.copy(profileLce = lceState) }
  }
}

internal class ProfileViewModelFactory @Inject constructor(
  private val profileParams: ProfileParams,
  private val profileUseCase: ProfileUseCase,
  private val companyUseCase: CompanyUseCase,
  private val dispatchersProvider: DispatchersProvider
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    require(modelClass == ProfileViewModel::class.java)
    return ProfileViewModel(profileParams, profileUseCase, companyUseCase, dispatchersProvider) as T
  }
}