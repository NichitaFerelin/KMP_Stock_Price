package com.ferelin.features.home.home

import androidx.compose.runtime.Immutable
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

@Immutable
internal data class HomeStateUi(
  val selectedScreenIndex: Int = STOCKS_SCREEN_INDEX
)

internal class HomeViewModel : ViewModel() {
  private val viewModelState = MutableStateFlow(HomeStateUi())
  val uiState = viewModelState.asStateFlow()

  fun onScreenSelected(screenIndex: Int) {
    viewModelState.update { it.copy(selectedScreenIndex = screenIndex) }
  }
}