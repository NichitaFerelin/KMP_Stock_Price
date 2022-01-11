package com.ferelin.features.about.ui.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
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
  private val dispatchersProvider: DispatchersProvider,
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
      .flowOn(dispatchersProvider.IO)
      .launchIn(viewModelScope)
  }

  fun onFavouriteIconClick() {
    viewModelScope.launch(dispatchersProvider.IO) {
      onFavouriteSwitchEvent.emit(Unit)
    }
  }

  fun onBackBtnClick() {
    coordinator.onEvent(AboutScreenEvent.BackRequested)
  }

  private suspend fun switchRequested(isFavourite: Boolean) {
    if (isFavourite) {
      favouriteCompanyUseCase.removeFromFavourite(aboutParams.companyId)
    } else {
      favouriteCompanyUseCase.addToFavourite(aboutParams.companyId)
    }
  }
}

internal class AboutViewModelFactory @AssistedInject constructor(
  @Assisted(ABOUT_PARAMS) private val params: AboutParams,
  private val coordinator: Coordinator,
  private val favouriteCompanyUseCase: FavouriteCompanyUseCase,
  private val dispatchersProvider: DispatchersProvider
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    require(modelClass == AboutViewModel::class.java)
    return AboutViewModel(favouriteCompanyUseCase, coordinator, dispatchersProvider, params) as T
  }

  @AssistedFactory
  interface Factory {
    fun create(@Assisted(ABOUT_PARAMS) aboutParams: AboutParams): AboutViewModelFactory
  }
}

internal const val ABOUT_PARAMS = "about-params"