package com.ferelin.features.about.ui.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ferelin.core.ui.params.AboutParams
import com.ferelin.core.domain.entities.FavouriteCompanyUseCase
import com.ferelin.navigation.Router
import kotlinx.coroutines.flow.map
import javax.inject.Inject

internal class AboutViewModel @Inject constructor(
  private val router: Router,
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

internal class AboutViewModelFactory @Inject constructor(
  var params: AboutParams? = null,
  private val router: Router,
  private val favouriteCompanyUseCase: FavouriteCompanyUseCase,
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    return AboutViewModel(
      router,
      params!!,
      favouriteCompanyUseCase
    ) as T
  }
}