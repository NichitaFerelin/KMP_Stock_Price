package com.ferelin.stockprice.androidApp.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import com.ferelin.stockprice.androidApp.ui.ViewModelWrapper
import com.ferelin.stockprice.shared.ui.viewModel.CryptosStateUi
import com.ferelin.stockprice.shared.ui.viewModel.CryptosViewModel
import com.ferelin.stockprice.sharedComposables.components.CryptosList
import org.koin.androidx.compose.getViewModel

@Composable
fun CryptosRoute() {
  val viewModelWrapper = getViewModel<ViewModelWrapper>()
  val viewModel: CryptosViewModel = remember { viewModelWrapper.viewModel() }
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