package com.ferelin.features.about.about

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.ferelin.core.coroutine.DispatchersProvider
import com.ferelin.core.domain.usecase.FavouriteCompanyUseCase
import com.ferelin.core.ui.params.AboutParams
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

internal data class AboutStateUi(
  val companyTicker: String,
  val companyName: String,
  val isFavourite: Boolean = false,
  val selectedScreenIndex: Int = PROFILE_INDEX
)

internal class AboutViewModel(
  private val aboutParams: AboutParams,
  private val favouriteCompanyUseCase: FavouriteCompanyUseCase,
  private val dispatchersProvider: DispatchersProvider,
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
      .subscribeOn(Schedulers.io())
      .observeOn(Schedulers.io())
      .subscribe(this::onCompanyIsFavourite) { Timber.e(it) }
  }

  fun onScreenSelected(index: Int) {
    viewModelState.update { it.copy(selectedScreenIndex = index) }
  }

  fun switchFavourite() {
    viewModelScope.launch(dispatchersProvider.IO) {
      onFavouriteSwitchEvent.emit(Unit)
    }
  }

  private fun switchRequested(isFavourite: Boolean) {
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
  override fun <T : ViewModel> create(modelClass: Class<T>): T {
    require(modelClass == AboutViewModel::class.java)
    return AboutViewModel(params, favouriteCompanyUseCase, dispatchersProvider) as T
  }
}