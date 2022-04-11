package com.ferelin.stockprice.shared.ui.viewModel

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class HomeStateUi internal constructor(
    val selectedScreenIndex: Int = SCREEN_STOCKS_INDEX
)

class HomeViewModel internal constructor() {
    private val viewModelState = MutableStateFlow(HomeStateUi())
    val uiState = viewModelState.asStateFlow()

    fun onScreenSelected(screenIndex: Int) {
        viewModelState.update { it.copy(selectedScreenIndex = screenIndex) }
    }
}

const val HOME_TOTAL_SCREENS = 3
const val SCREEN_CRYPTOS_INDEX = 0
const val SCREEN_STOCKS_INDEX = 1
const val SCREEN_FAVOURITE_STOCKS_INDEX = 2