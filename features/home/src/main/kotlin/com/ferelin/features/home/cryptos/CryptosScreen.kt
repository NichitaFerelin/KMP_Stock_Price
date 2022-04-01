package com.ferelin.features.home.cryptos

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.ferelin.features.home.uiComponents.CryptosList
import org.koin.androidx.compose.getViewModel

@Composable
fun CryptosRoute() {
  val viewModel = getViewModel<CryptosViewModel>()
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