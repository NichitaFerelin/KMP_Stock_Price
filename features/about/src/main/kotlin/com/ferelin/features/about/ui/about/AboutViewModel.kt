package com.ferelin.features.about.ui.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.core.ui.params.AboutParams
import com.ferelin.core.ui.view.routing.Coordinator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

internal class AboutViewModel(
  private val favouriteCompanyUseCase: FavouriteCompanyUseCase,
  private val coordinator: Coordinator,
  val aboutParams: AboutParams,
) : ViewModel() {
  private val onFavouriteSwitchEvent = MutableSharedFlow<Unit>()

  val isCompanyFavourite = favouriteCompanyUseCase.favouriteCompanies
    .map { companies -> companies.find { it == aboutParams.companyId } != null }

  init {
    onFavouriteSwitchEvent
      .zip(
        other = isCompanyFavourite,
        transform = { _, isFavourite -> isFavourite }
      )
      .onEach(this::switchRequested)
      .launchIn(viewModelScope)
  }

  fun onFavouriteIconClick() {
    viewModelScope.launch { onFavouriteSwitchEvent.emit(Unit) }
  }

  fun onBackBtnClick() {
    coordinator.onEvent(AboutRouteEvents.Event.BackRequested)
  }

  private suspend fun switchRequested(isFavourite: Boolean) {
    if (isFavourite) {
      favouriteCompanyUseCase.addToFavourite(aboutParams.companyId)
    } else {
      favouriteCompanyUseCase.removeFromFavourite(aboutParams.companyId)
    }
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
    return AboutViewModel(favouriteCompanyUseCase, coordinator, params) as T
  }

  @AssistedFactory
  interface Factory {
    fun create(@Assisted(ABOUT_PARAMS) aboutParams: AboutParams): AboutViewModelFactory
  }
}

internal const val ABOUT_PARAMS = "about-params"