package com.ferelin.features.about.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ferelin.core.domain.entity.LceState
import com.ferelin.core.domain.usecase.CompanyUseCase
import com.ferelin.core.domain.usecase.ProfileUseCase
import com.ferelin.core.ui.params.ProfileParams
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.*
import javax.inject.Inject

internal data class ProfileStateUi(
  val profile: ProfileViewData = ProfileViewData(),
  val profileLce: LceState = LceState.None
)

internal class ProfileViewModel(
  profileParams: ProfileParams,
  profileUseCase: ProfileUseCase,
  companyUseCase: CompanyUseCase
) : ViewModel() {
  private val viewModelState = MutableStateFlow(ProfileStateUi())
  val uiState = viewModelState.asStateFlow()

  init {
    companyUseCase.companies
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.io())
      .map { companies -> companies.find { it.id == profileParams.companyId }!! }
      .zipWith(profileUseCase.getProfileBy(profileParams.companyId)) { company, profile ->
        ProfileMapper.map(profile, company)
      }
      .subscribe(this::onProfile)

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
  private val companyUseCase: CompanyUseCase
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    require(modelClass == ProfileViewModel::class.java)
    return ProfileViewModel(profileParams, profileUseCase, companyUseCase) as T
  }
}