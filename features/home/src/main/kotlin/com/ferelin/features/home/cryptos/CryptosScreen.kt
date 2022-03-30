package com.ferelin.features.home.cryptos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.ferelin.features.home.uiComponents.CryptosList

@Composable
fun CryptosRoute(
  deps: CryptosDeps
) {
  val componentViewModel = viewModel<CryptosComponentViewModel>(
    factory = CryptosComponentViewModelFactory(deps)
  )
  val viewModel = viewModel<CryptosViewModel>(
    factory = componentViewModel.component.viewModelFactory()
  )
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