package com.ferelin.features.splash.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ferelin.core.ui.view.routing.Coordinator
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

internal class LoadingViewModel @Inject constructor(
  private val coordinator: Coordinator
) : ViewModel() {
  private val _launchTrigger = MutableStateFlow(false)
  val launchTrigger: Flow<Boolean> = _launchTrigger.asStateFlow()

  init {
    viewModelScope.launch {
      delay(SPLASH_SCREEN_LIFE_TIME)
      _launchTrigger.value = true
    }
  }

  fun onPrepared() {
    coordinator.onEvent(LoadingRouteEvent.Loaded)
  }
}