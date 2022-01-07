package com.ferelin.features.about.ui.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ferelin.core.domain.entities.CompanyUseCase
import com.ferelin.core.ui.params.ProfileParams
import com.ferelin.features.about.domain.ProfileUseCase
import com.ferelin.navigation.Router
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.zip
import javax.inject.Inject

internal class ProfileViewModel(
  private val router: Router,
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

  fun onPhoneClick(phone: String): Boolean {
    // open contacts
    return true
  }

  fun onUrlClick(): Boolean {
    // open url
    return true
  }
}

internal class ProfileViewModelFactory @Inject constructor(
  var profileParams: ProfileParams? = null,
  private val router: Router,
  private val profileUseCase: ProfileUseCase,
  private val companyUseCase: CompanyUseCase
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return ProfileViewModel(
      router,
      profileParams!!,
      profileUseCase,
      companyUseCase
    ) as T
  }
}