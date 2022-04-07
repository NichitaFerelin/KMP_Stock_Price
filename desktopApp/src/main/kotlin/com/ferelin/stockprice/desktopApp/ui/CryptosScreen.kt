package com.ferelin.stockprice.desktopApp.ui

import androidx.compose.runtime.*
import com.ferelin.stockprice.desktopApp.ViewModelWrapper
import com.ferelin.stockprice.shared.ui.viewModel.CryptosStateUi
import com.ferelin.stockprice.shared.ui.viewModel.CryptosViewModel
import com.ferelin.stockprice.sharedComposables.components.CryptosList

@Composable
internal fun CryptosScreenRoute() {
  val viewModelScope = rememberCoroutineScope()
  val viewModel: CryptosViewModel = remember {
    ViewModelWrapper().viewModel(viewModelScope)
  }
  val uiState by viewModel.uiState.collectAsState()

  CryptosScreen(uiState = uiState)
}

@Composable
private fun CryptosScreen(
  uiState: CryptosStateUi
) {
  CryptosList(
    cryptos = uiState.cryptos,
    cryptosLce = uiState.cryptosLce
  )
}