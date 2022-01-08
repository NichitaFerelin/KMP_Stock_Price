package com.ferelin.features.about.ui.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.core.ui.params.AboutParams
import com.ferelin.core.ui.view.routing.Coordinator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.map

internal class AboutViewModel(
  private val coordinator: Coordinator,
  val aboutParams: AboutParams,
  favouriteCompanyUseCase: FavouriteCompanyUseCase,
) : ViewModel() {
  val isCompanyFavourite = favouriteCompanyUseCase.favouriteCompanies
    .map { companies -> companies.find { it == aboutParams.companyId } != null }

  fun onFavouriteIconClick() {
    // remove or add to favourites
  }

  fun onBackBtnClick() {
    // navigate back
  }
}

internal class AboutViewModelFactory @AssistedInject constructor(
  @Assisted(ABOUT_PARAMS) private val params: AboutParams,
  private val coordinator: Coordinator,
  private val favouriteCompanyUseCase: FavouriteCompanyUseCase,
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    require(modelClass == AboutViewModel::class)
    return AboutViewModel(coordinator, params, favouriteCompanyUseCase) as T
  }

  @AssistedFactory
  interface Factory {
    fun create(@Assisted(ABOUT_PARAMS) aboutParams: AboutParams): AboutViewModelFactory
  }
}

internal const val ABOUT_PARAMS = "about-params"