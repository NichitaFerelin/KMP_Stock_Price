package com.ferelin.stockprice.shared.ui.viewModel

import com.ferelin.stockprice.androidApp.domain.usecase.CompanyUseCase
import com.ferelin.stockprice.androidApp.domain.usecase.ProfileUseCase
import com.ferelin.stockprice.androidApp.domain.entity.CompanyId
import com.ferelin.stockprice.androidApp.domain.entity.LceState
import com.ferelin.stockprice.androidApp.ui.DispatchersProvider
import com.ferelin.stockprice.androidApp.ui.mapper.ProfileMapper
import com.ferelin.stockprice.androidApp.ui.params.ProfileParams
import com.ferelin.stockprice.androidApp.ui.viewData.ProfileViewData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*

data class ProfileStateUi(
  val profile: ProfileViewData = ProfileViewData(),
  val profileLce: LceState = LceState.None
)

class ProfileViewModel(
  profileParams: ProfileParams,
  profileUseCase: ProfileUseCase,
  companyUseCase: CompanyUseCase,
  viewModelScope: CoroutineScope,
  dispatchersProvider: DispatchersProvider
) {
  private val viewModelState = MutableStateFlow(ProfileStateUi())
  val uiState = viewModelState.asStateFlow()

  init {
    companyUseCase.companies
      .map { companies -> companies.find { it.id.value == profileParams.companyId } }
      .filterNotNull()
      .zip(
        other = profileUseCase.getProfileBy(CompanyId(profileParams.companyId)),
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