package com.ferelin.features.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.core.ui.params.AboutParams
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

internal data class AboutStateUi(
  val companyTicker: String,
  val companyName: String,
  val isFavourite: Boolean = false,
  val selectedScreenIndex: Int = PROFILE_INDEX
)

internal class AboutViewModel(
  private val favouriteCompanyUseCase: FavouriteCompanyUseCase,
  private val dispatchersProvider: DispatchersProvider,
  private val aboutParams: AboutParams,
) : ViewModel() {
  private val viewModelState = MutableStateFlow(
    value = AboutStateUi(
      companyTicker = aboutParams.companyTicker,
      companyName = aboutParams.companyName
    )
  )
  val uiState = viewModelState.asStateFlow()

  private val onFavouriteSwitchEvent = MutableSharedFlow<Unit>()
  private val isCompanyFavourite = favouriteCompanyUseCase.favouriteCompanies
    .map { companies -> companies.find { it == aboutParams.companyId } != null }

  init {
    isCompanyFavourite
      .onEach(this::onCompanyIsFavourite)
      .flowOn(dispatchersProvider.IO)
      .launchIn(viewModelScope)

    onFavouriteSwitchEvent
      .zip(
        other = isCompanyFavourite,
        transform = { _, isFavourite -> isFavourite }
      )
      .onEach(this::switchRequested)
      .flowOn(dispatchersProvider.IO)
      .launchIn(viewModelScope)
  }

  fun onScreenSelected(index: Int) {
    viewModelState.update { it.copy(selectedScreenIndex = index) }
  }

  fun switchFavourite() {
    viewModelScope.launch(dispatchersProvider.IO) {
      onFavouriteSwitchEvent.emit(Unit)
    }
  }

  private suspend fun switchRequested(isFavourite: Boolean) {
    if (isFavourite) {
      favouriteCompanyUseCase.removeFromFavourite(aboutParams.companyId)
    } else {
      favouriteCompanyUseCase.addToFavourite(aboutParams.companyId)
    }
  }

  private fun onCompanyIsFavourite(isFavourite: Boolean) {
    viewModelState.update { it.copy(isFavourite = isFavourite) }
  }
}

internal class AboutViewModelFactory @Inject constructor(
  private val params: AboutParams,
  private val favouriteCompanyUseCase: FavouriteCompanyUseCase,
  private val dispatchersProvider: DispatchersProvider
) : ViewModelProvider.Factory {
  @Suppress("UNCHECKED_CAST")
  override fun <T : ViewModel?> create(modelClass: Class<T>): T {
    require(modelClass == AboutViewModel::class.java)
    return AboutViewModel(favouriteCompanyUseCase, dispatchersProvider, params) as T
  }
}