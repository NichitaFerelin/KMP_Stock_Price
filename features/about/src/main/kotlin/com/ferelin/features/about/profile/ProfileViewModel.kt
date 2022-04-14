package com.ferelin.features.about.profile

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.ui.params.ProfileParams
import kotlinx.coroutines.flow.*

@Immutable
internal data class ProfileStateUi(
  val profile: ProfileViewData = ProfileViewData(),
  val profileLce: LceState = LceState.None
)

internal class ProfileViewModel(
  profileParams: ProfileParams,
  companyUseCase: CompanyUseCase,
  dispatchersProvider: DispatchersProvider
) : ViewModel() {
  private val viewModelState = MutableStateFlow(ProfileStateUi())
  val uiState = viewModelState.asStateFlow()

  init {
    /*companyUseCase.companies
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
      .launchIn(viewModelScope)*/
  }

  private fun onProfile(profileViewData: ProfileViewData) {
    viewModelState.update { it.copy(profile = profileViewData) }
  }

  private fun onProfileLce(lceState: LceState) {
    viewModelState.update { it.copy(profileLce = lceState) }
  }
}